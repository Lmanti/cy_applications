package co.com.crediya.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loanstatus.gateways.LoanStatusRepository;
import co.com.crediya.r2dbc.entity.LoanStatusEntity;
import co.com.crediya.r2dbc.exception.DataRetrievalException;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class LoanStatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    LoanStatus,
    LoanStatusEntity,
    Integer,
    LoanStatusReactiveRepository
> implements LoanStatusRepository {
    public LoanStatusReactiveRepositoryAdapter(LoanStatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanStatus.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LoanStatus> getAllLoanStatuses() {
        log.info("Retrieving all loan statuses");
    
        return findAll()
            .doOnNext(loanStatus
             -> log.debug("Retrieved loan statuses successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all loan statuses"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all loan statuses", ex);
                return new DataRetrievalException("Error al momento de consultar los estados del crédito", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve all the loan statuses", ex));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LoanStatus> getLoanStatusById(int loanStatusId) {
        log.info("Searching loan status with ID number: {}", loanStatusId);
    
        return repository.findById(loanStatusId)
            .doOnNext(entity -> log.debug("Found loan status entity ID number {}", loanStatusId))
            .map(this::toEntity)
            .doOnNext(entity -> log.info("Successfully mapped loan status ID number {}", loanStatusId))
            .onErrorMap(ex -> {
                log.error("Error retrieving loan status with ID number {}: {}", loanStatusId, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando estado de crédito con ID " + loanStatusId, ex);
            });
    }
    
}
