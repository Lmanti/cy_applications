package co.com.crediya.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import co.com.crediya.model.application.record.ApplicationRecord;
import co.com.crediya.api.dto.CreateApplicationDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private Handler handler;

    private RouterRest routerRest;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldRouteGetAllApplicationsToHandler() {
        // Given
        ApplicationRecord applicationRecord = createSampleApplicationRecord();
        List<ApplicationRecord> applications = Collections.singletonList(applicationRecord);
        when(handler.getAllApplications(any())).thenReturn(ServerResponse.ok().bodyValue(applications));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/solicitud")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ApplicationRecord.class)
            .hasSize(1);

        verify(handler).getAllApplications(any());
    }

    @Test
    void shouldRouteCreateApplicationToHandler() {
        // Given
        CreateApplicationDTO createApplicationDTO = createSampleCreateApplicationDTO();
        ApplicationRecord applicationRecord = createSampleApplicationRecord();

        when(handler.createApplication(any())).thenReturn(
            ServerResponse.created(URI.create("/applicationDetails/" + applicationRecord.applicationId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(applicationRecord)
        );

        // When & Then
        webTestClient.post()
            .uri("/api/v1/solicitud")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createApplicationDTO)
            .exchange()
            .expectStatus().isCreated();

        verify(handler).createApplication(any());
    }

    // Helper methods para crear objetos de prueba
    private ApplicationRecord createSampleApplicationRecord() {
        return new ApplicationRecord(
            UUID.randomUUID(),
            123456789L,
            1000000.0,
            12.0,
            null, // Mock LoanType
            null  // Mock LoanStatus
        );
    }

    private CreateApplicationDTO createSampleCreateApplicationDTO() {
        return new CreateApplicationDTO(
            123456789L,
            1000000.0,
            12.0,
            1 // LoanTypeId
        );
    }
}