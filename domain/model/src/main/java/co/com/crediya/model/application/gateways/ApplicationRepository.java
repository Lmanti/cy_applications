package co.com.crediya.model.application.gateways;

import java.util.UUID;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.SearchCriteria;
import co.com.crediya.model.application.criteria.PageResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Flux<Application> getAllApplications();
    Flux<Application> getApplicationsByUserEmail(String userEmail);
    Mono<Application> getApplicationsByApplicationId(UUID applicationId);
    Mono<Application> saveApplication(Mono<Application> application);
    Mono<PageResult<Application>> findByCriteria(SearchCriteria criteria);
    Mono<Application> updateApplication(Mono<Application> application);
}
