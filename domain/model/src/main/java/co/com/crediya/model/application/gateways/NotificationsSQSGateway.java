package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.record.ApplicationRecord;
import reactor.core.publisher.Mono;

public interface NotificationsSQSGateway {
    Mono<String> send(ApplicationRecord updatedApplication);
}
