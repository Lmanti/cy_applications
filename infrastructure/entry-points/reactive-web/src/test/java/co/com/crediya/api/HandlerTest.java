package co.com.crediya.api;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.api.mapper.ApplicationDTOMapper;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.record.ApplicationRecord;
import co.com.crediya.usecase.application.ApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private ApplicationUseCase applicationUseCase;

    @Mock
    private ApplicationDTOMapper applicationMapper;

    @InjectMocks
    private Handler handler;

    private CreateApplicationDTO createApplicationDTO;
    @SuppressWarnings("unused")
    private Application application;
    private ApplicationRecord applicationRecord;

    @BeforeEach
    void setUp() {
        createApplicationDTO = new CreateApplicationDTO(123456789l, 1000000.0, 12.0, 1);
        application = new Application(UUID.randomUUID(),123456789l, 1000000.0, 12.0, 1, 1);
        applicationRecord = new ApplicationRecord(UUID.randomUUID(), "test@test.com", 1000000.0, 12.0, null, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    void createApplication_success() {
        when(applicationUseCase.saveApplication(any(Mono.class))).thenReturn(Mono.just(applicationRecord));

        ServerRequest serverRequest = MockServerRequest.builder()
            .body(Mono.just(createApplicationDTO));

        Mono<ServerResponse> response = handler.createApplication(serverRequest);

        StepVerifier.create(response)
            .assertNext(serverResponse -> {
                assertEquals(201, serverResponse.statusCode().value());
                assertEquals(MediaType.APPLICATION_JSON, serverResponse.headers().getContentType());
                assertEquals(URI.create("/detalleSolicitud/" + applicationRecord.applicationId()), serverResponse.headers().getLocation());
            })
            .expectComplete();
    }

    @Test
    void getAllApplications_success() {
        when(applicationUseCase.getAllApplications()).thenReturn(Mono.just(applicationRecord).flux());

        ServerRequest serverRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> response = handler.getAllApplications(serverRequest);

        StepVerifier.create(response)
            .assertNext(serverResponse -> {
                assertEquals(200, serverResponse.statusCode().value());
            })
            .expectComplete();
    }

    @Test
    void getAllApplications_emptyList() {
        when(applicationUseCase.getAllApplications()).thenReturn(Flux.empty());

        ServerRequest serverRequest = MockServerRequest.builder().build();

        Mono<ServerResponse> response = handler.getAllApplications(serverRequest);

        StepVerifier.create(response)
            .assertNext(serverResponse -> {
                assertEquals(200, serverResponse.statusCode().value());
            })
            .expectComplete();
    }
}