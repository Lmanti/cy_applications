package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entity.ApplicationEntity;
import co.com.crediya.r2dbc.exception.DataPersistenceException;
import co.com.crediya.r2dbc.exception.DataRetrievalException;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Application,
    ApplicationEntity,
    UUID,
    ApplicationReactiveRepository
> implements ApplicationRepository {
    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Application.class));
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
    public Flux<Application> getApplicationsByUserIdNumber(Long userIdNumber) {
        log.info("Searching applications of user with ID number: {}", userIdNumber);
    
        return repository.findByUserIdNumber(userIdNumber)
            .doOnNext(entities -> log.debug("Found applications entites of user with ID number {}", userIdNumber))
            .map(this::toEntity)
            .doOnNext(entities -> log.info("Successfully mapped applications of user with ID number {}", userIdNumber))
            .onErrorMap(ex -> {
                log.error("Error retrieving applications of user with ID number {}: {}", userIdNumber, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando las solicitudes del usuario con identificación " + userIdNumber, ex);
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

}
