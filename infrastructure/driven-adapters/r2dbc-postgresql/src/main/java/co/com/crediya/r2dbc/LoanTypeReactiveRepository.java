package co.com.crediya.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.r2dbc.entity.LoanTypeEntity;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Integer>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
    
}
