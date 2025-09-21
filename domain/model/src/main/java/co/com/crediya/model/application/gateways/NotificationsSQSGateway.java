package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.record.ApplicationWithUserInfoRecord;
import reactor.core.publisher.Mono;

public interface NotificationsSQSGateway {
    Mono<String> send(ApplicationWithUserInfoRecord updatedApplication);
}
