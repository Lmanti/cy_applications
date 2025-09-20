package co.com.crediya.sqs.sender;

import co.com.crediya.model.application.exception.SQSMessagingException;
import co.com.crediya.model.application.gateways.NotificationsSQSGateway;
import co.com.crediya.model.application.record.ApplicationRecord;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import co.com.crediya.sqs.sender.utility.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationsSQSGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final JsonConverter jsonConverter;

    @Override
    public Mono<String> send(ApplicationRecord updatedApplication) {
        return Mono.fromCallable(() -> buildRequest(updatedApplication))
            .flatMap(request -> Mono.fromFuture(client.sendMessage(request))
                .onErrorResume(e -> {
                    log.error("Error sending message to SQS: {}", e.getMessage(), e);
                    return Mono.error(new SQSMessagingException("Error al enviar el mensaje a SQS", e));
                }))
            .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
            .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(ApplicationRecord message) {
        String jsonMessage = jsonConverter.toJson(message);
        
        return SendMessageRequest.builder()
            .queueUrl(properties.queueUrl())
            .messageBody(jsonMessage)
            .build();
    }
}
