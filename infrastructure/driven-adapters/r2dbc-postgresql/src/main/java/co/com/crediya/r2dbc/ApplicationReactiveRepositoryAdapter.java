package co.com.crediya.r2dbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.PageResult;
import co.com.crediya.model.application.criteria.SearchCriteria;
import co.com.crediya.model.application.exception.DataPersistenceException;
import co.com.crediya.model.application.exception.DataRetrievalException;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entity.ApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Application,
    ApplicationEntity,
    UUID,
    ApplicationReactiveRepository
> implements ApplicationRepository {
    private final DatabaseClient databaseClient;

    private static final Set<String> ALLOWED_COLUMNS = Set.of(
        "user_email", "loan_type_id", "loan_status_id"
    );

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
        "application_id", "user_email", "loan_amount", "loan_term"
    );

    public ApplicationReactiveRepositoryAdapter(
        ApplicationReactiveRepository repository,
        ObjectMapper mapper,
        DatabaseClient databaseClient
    ) {
        super(repository, mapper, d -> mapper.map(d, Application.class));
        this.databaseClient = databaseClient;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Application> getAllApplications() {
        log.info("Retrieving all applications");
    
        return findAll()
            .doOnNext(application
                -> log.debug("Retrieved applications successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all applications"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all applications", ex);
                return new DataRetrievalException("Error al momento de consultar las solicitudes", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve all the applications", ex));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Application> getApplicationsByUserEmail(String userEmail) {
        log.info("Searching applications of user with email: {}", userEmail);
    
        return repository.findByUserEmail(userEmail)
            .doOnNext(entities -> log.debug("Found applications entites of user with email {}", userEmail))
            .map(this::toEntity)
            .doOnNext(entities -> log.info("Successfully mapped applications of user with email {}", userEmail))
            .onErrorMap(ex -> {
                log.error("Error retrieving applications of user with email {}: {}", userEmail, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando las solicitudes del usuario con email " + userEmail, ex);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Application> getApplicationsByApplicationId(UUID applicationId) {
        log.info("Searching application with ID number: {}", applicationId);
    
        return repository.findById(applicationId)
            .doOnNext(entity -> log.debug("Found application entity ID number {}", applicationId))
            .map(this::toEntity)
            .doOnNext(entity -> log.info("Successfully mapped application ID number {}", applicationId))
            .onErrorMap(ex -> {
                log.error("Error retrieving application with ID number {}: {}", applicationId, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando la solicitud con ID " + applicationId, ex);
            });
    }

    @Override
    @Transactional(rollbackFor = DataPersistenceException.class)
    public Mono<Application> saveApplication(Mono<Application> application) {
        return application
            .doOnNext(applicationData -> log.info("Attempting to save application: {}", applicationData.getApplicationId()))
            .flatMap(applicationData -> 
                repository.save(toData(applicationData))
                    .doOnNext(savedEntity -> log.debug("Application entity saved: {}", savedEntity.getApplicationId()))
                    .map(this::toEntity)
                    .doOnNext(savedApplication -> log.info("Application successfully saved: {}", savedApplication.getApplicationId()))
                    .onErrorMap(ex -> {
                        log.error("Error saving application: {}", ex.getMessage(), ex);
                        return new DataPersistenceException("Error intentando guardar la solicitud", ex);
                    })
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PageResult<Application>> findByCriteria(SearchCriteria criteria) {
        log.info("Searching applications matching criteria: {}", criteria);
        
        StringBuilder sql = new StringBuilder("SELECT * FROM applications WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM applications WHERE 1=1");

        List<Object> params = new ArrayList<>();
        List<Object> countParams = new ArrayList<>();

        if (criteria.getFilters() != null) {
            criteria.getFilters().forEach((key, value) -> {
                if (!ALLOWED_COLUMNS.contains(key)) {
                    throw new IllegalArgumentException("Filtro inválido en columna: " + key);
                }
                sql.append(" AND ").append(key).append(" = $").append(params.size() + 1);
                countSql.append(" AND ").append(key).append(" = $").append(countParams.size() + 1);
                params.add(value);
                countParams.add(value);
            });
        }

        if (criteria.getSortBy() != null) {
            if (!ALLOWED_SORT_COLUMNS.contains(criteria.getSortBy())) {
                throw new IllegalArgumentException("Ordenamiento inválido en columna: " + criteria.getSortBy());
            }
            sql.append(" ORDER BY ").append(criteria.getSortBy())
            .append(" ").append(criteria.getSortDirection() != null ? criteria.getSortDirection() : "ASC");
        }

        sql.append(" LIMIT $").append(params.size() + 1).append(" OFFSET $").append(params.size() + 2);
        params.add(criteria.getSize());
        params.add(criteria.getPage() * criteria.getSize());

        Flux<ApplicationEntity> dataFlux = executeQuery(sql.toString(), params);
        Mono<Long> countMono = executeCountQuery(countSql.toString(), countParams);

        return Mono.zip(dataFlux.collectList(), countMono)
            .map(tuple -> {
            List<Application> applications = tuple.getT1().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

            return new PageResult<Application>(
                applications,
                tuple.getT2(),
                criteria.getPage(), 
                criteria.getSize()
            );
        })
        .doOnError(error -> {
            log.error("Error executing search criteria: {}", criteria, error);
            throw new DataRetrievalException("Error intentando guardar la solicitud", error);
        });
    }

    private Flux<ApplicationEntity> executeQuery(String sql, List<Object> params) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);
        for (int i = 0; i < params.size(); i++) {
            spec = spec.bind(i, params.get(i));
        }
        return spec.map((row, rowMetadata) -> mapRow(row)).all();
    }

    private Mono<Long> executeCountQuery(String sql, List<Object> params) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);
        for (int i = 0; i < params.size(); i++) {
            spec = spec.bind(i, params.get(i));
        }
        return spec.map((row, rowMetadata) -> row.get(0, Long.class)).first();
    }

    private ApplicationEntity mapRow(Row row) {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setApplicationId(row.get("application_id", UUID.class));
        entity.setUserEmail(row.get("user_email", String.class));
        entity.setLoanAmount(row.get("loan_amount", Double.class));
        entity.setLoanTerm(row.get("loan_term", Double.class));
        entity.setLoanTypeId(row.get("loan_type_id", Integer.class));
        entity.setLoanStatusId(row.get("loan_status_id", Integer.class));
        return entity;
    }

    @Override
    public Mono<Application> updateApplication(Mono<Application> application) {
        return application
            .doOnNext(applicationData -> log.info("Attempting to update an application: {}", applicationData.getApplicationId()))
            .flatMap(applicationData -> 
                repository.save(toData(applicationData))
                    .doOnNext(updatedEntity -> log.debug("Application entity updated: {}", updatedEntity.getApplicationId()))
                    .map(this::toEntity)
                    .doOnNext(updatedApplication -> log.info("Application successfully updated: {}", updatedApplication.getApplicationId()))
                    .onErrorMap(ex -> {
                        log.error("Error updating an application: {}", ex.getMessage(), ex);
                        return new DataPersistenceException("Error intentando editar la solicitud", ex);
                    })
            );
    }
}
