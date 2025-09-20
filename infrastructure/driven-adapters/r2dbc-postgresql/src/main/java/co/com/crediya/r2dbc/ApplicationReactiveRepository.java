package co.com.crediya.r2dbc;

import java.util.UUID;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.r2dbc.entity.ApplicationEntity;
import reactor.core.publisher.Flux;

public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<ApplicationEntity> {
    Flux<ApplicationEntity> findByUserEmail(String userEmail);
}
