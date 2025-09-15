package co.com.crediya.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.model.application.exception.ServiceNotAvailabeException;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.application.record.UserBasicInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @CircuitBreaker(name = "existByIdNumber" , fallbackMethod = "existByIdNumberFallback")
    @Override
    public Mono<Boolean> existByIdNumber(Long userIdNumber) {
        log.info("Validating if user with ID number {} exists", userIdNumber);      
        return client.get()
            .uri(uriBuilder -> 
                uriBuilder.path("exists/{userIdNumber}").build(userIdNumber))
            .retrieve()
            .bodyToMono(Boolean.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    @CircuitBreaker(name = "getAllUsersBasicInfo" , fallbackMethod = "getAllUsersBasicInfoFallback")
    @Override
    public Flux<UserBasicInfo> getAllUsersBasicInfo() {
        log.info("Attempting to retrieve all users's basic info");      
        return client.get()
            .uri("basicInfo")
            .retrieve()
            .bodyToFlux(UserBasicInfo.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    public Mono<Boolean> existByIdNumberFallback(Long userIdNumber, Exception e) {
        log.error("Circuit breaker fallback: Error checking if user exists: {}", e.getMessage());
        return Mono.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }

    public Flux<UserBasicInfo> getAllUsersBasicInfoFallback(Exception e) {
        log.error("Circuit breaker fallback: Error getting all users basic info: {}", e.getMessage());
        return Flux.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }
}
