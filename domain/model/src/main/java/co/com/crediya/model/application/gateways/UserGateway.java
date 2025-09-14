package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.record.UserBasicInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<Boolean> existByIdNumber(Long userIdNumber);
    Flux<UserBasicInfo> getAllUsersBasicInfo();
}
