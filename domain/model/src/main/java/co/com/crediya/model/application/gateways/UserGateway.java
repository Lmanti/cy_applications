package co.com.crediya.model.application.gateways;

import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<Boolean> existByIdNumber(Long userIdNumber);
}
