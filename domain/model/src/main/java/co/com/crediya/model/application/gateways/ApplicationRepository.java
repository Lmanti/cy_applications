package co.com.crediya.model.application.gateways;

import java.util.UUID;

import co.com.crediya.model.application.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Flux<Application> getAllApplications();
    Flux<Application> getApplicationsByUserIdNumber(Long userIdNumber);
    Mono<Application> getApplicationsByApplicationId(UUID applicationId);
    Mono<Application> saveApplication(Mono<Application> application);
}
