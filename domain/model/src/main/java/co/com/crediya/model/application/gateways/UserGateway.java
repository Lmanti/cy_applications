package co.com.crediya.model.application.gateways;

import java.util.List;

import co.com.crediya.model.application.record.UserBasicInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<UserBasicInfo> getUserByIdNumber(Long idNumber);
    Mono<UserBasicInfo> getUserByEmail(String userEmail);
    Flux<UserBasicInfo> getUsersBasicInfo(List<String> usersEmails);
    Mono<UserBasicInfo> getRequestUserByToken();
}
