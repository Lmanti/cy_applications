package co.com.crediya.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.consumer.exception.ServiceNotAvailabeException;
import co.com.crediya.model.application.gateways.UserGateway;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @CircuitBreaker(name = "existByIdNumber" , fallbackMethod = "existByIdNumberFallback")
    @Override
    public Mono<Boolean> existByIdNumber(Long userIdNumber) {        
        return client.get()
            .uri(uriBuilder -> 
                uriBuilder.path("exists/{userIdNumber}").build(userIdNumber))
            .retrieve()
            .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> existByIdNumberFallback(Long userIdNumber, Exception e) {
        log.error("Circuit breaker fallback: Error checking if user exists: {}", e.getMessage());
        return Mono.error(new ServiceNotAvailabeException("Servicio de usuarios no disponible por el momento, por favor intente más tarde", e));
    }
}
