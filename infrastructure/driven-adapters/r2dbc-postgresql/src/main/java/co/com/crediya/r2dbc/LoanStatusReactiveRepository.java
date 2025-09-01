package co.com.crediya.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.r2dbc.entity.LoanStatusEntity;

public interface LoanStatusReactiveRepository extends ReactiveCrudRepository<LoanStatusEntity, Integer>, ReactiveQueryByExampleExecutor<LoanStatusEntity> {
    
}
