package co.com.crediya.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.exception.DataRetrievalException;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    LoanType,
    LoanTypeEntity,
    Integer,
    LoanTypeReactiveRepository
> implements LoanTypeRepository {
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LoanType> getAllLoanTypes() {
        log.info("Retrieving all loan types");
    
        return findAll()
            .doOnNext(loanType
             -> log.debug("Retrieved loan types successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all loan types"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all loan types", ex);
                return new DataRetrievalException("Error al momento de consultar los tipos de crédito", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve all the loan types", ex));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LoanType> getLoanTypeById(int loanTypeId) {
        log.info("Searching loan type with ID number: {}", loanTypeId);
    
        return repository.findById(loanTypeId)
            .doOnNext(entity -> log.debug("Found loan type entity ID number {}", loanTypeId))
            .map(this::toEntity)
            .doOnNext(entity -> log.info("Successfully mapped loan type ID number {}", loanTypeId))
            .onErrorMap(ex -> {
                log.error("Error retrieving loan type with ID number {}: {}", loanTypeId, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando tipo de crédito con ID " + loanTypeId, ex);
            });
    }
    
}
