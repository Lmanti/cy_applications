package co.com.crediya.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @CircuitBreaker(name = "getUserByEmail" , fallbackMethod = "getUserByEmailFallback")
    @Override
    public Mono<UserBasicInfo> getUserByEmail(String userEmail) {
        log.info("Validating if user with email {} exists", userEmail);      
        return client.get()
            .uri(uriBuilder -> 
                uriBuilder.path("{userEmail}").build(userEmail))
            .retrieve()
            .bodyToMono(UserBasicInfo.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    @CircuitBreaker(name = "getUsersBasicInfo" , fallbackMethod = "getUsersBasicInfoFallback")
    @Override
    public Flux<UserBasicInfo> getUsersBasicInfo(List<String> usersEmails) {
        log.info("Attempting to retrieve all users's basic info");      
        return client.post()
            .uri("infoUsuarios")
            .bodyValue(usersEmails)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToFlux(UserBasicInfo.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    @CircuitBreaker(name = "getRequestUserByToken" , fallbackMethod = "getRequestUserByTokenFallback")
    @Override
    public Mono<UserBasicInfo> getRequestUserByToken() {
        log.info("Validating if user exists by token");   
        return client.get()
            .uri("consultarPorToken")
            .retrieve()
            .bodyToMono(UserBasicInfo.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    @CircuitBreaker(name = "getUserByIdNumber" , fallbackMethod = "getUserByIdNumberFallback")
    @Override
    public Mono<UserBasicInfo> getUserByIdNumber(Long idNumber) {
        log.info("Validating if user with idNumber {} exists", idNumber);      
        return client.get()
            .uri(uriBuilder -> 
                uriBuilder.path("detallesUsuario/{idNumber}").build(idNumber))
            .retrieve()
            .bodyToMono(UserBasicInfo.class)
            .onErrorMap(exception -> {
                log.error("Error calling user service: {}", exception.getMessage(), exception);
                return new ServiceNotAvailabeException("User service unavailable", exception);
            });
    }

    public Mono<UserBasicInfo> getUserByEmailFallback(String userEmail, Exception e) {
        log.error("Circuit breaker fallback: Error checking if user exists: {}", e.getMessage());
        return Mono.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }

    public Mono<UserBasicInfo> getUserByIdNumberFallback(Long idNumber, Exception e) {
        log.error("Circuit breaker fallback: Error checking if user exists: {}", e.getMessage());
        return Mono.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }

    public Flux<UserBasicInfo> getUsersBasicInfoFallback(List<String> usersEmails, Exception e) {
        log.error("Circuit breaker fallback: Error getting all users basic info: {}", e.getMessage());
        return Flux.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }

    public Mono<UserBasicInfo> getRequestUserByTokenFallback(Exception e) {
        log.error("Circuit breaker fallback: Error getting all users basic info: {}", e.getMessage());
        return Mono.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }

}
