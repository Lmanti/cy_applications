package co.com.crediya.consumer;

import co.com.crediya.model.application.exception.ServiceNotAvailabeException;
import co.com.crediya.model.application.record.UserBasicInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    @Mock
    private WebClient client;

    @InjectMocks
    private RestConsumer restConsumer;

    private String userEmail;
    private UserBasicInfo userBasicInfo;
    private List<String> userEmails;

    @BeforeEach
    void setUp() {
        userEmail = "test@example.com";
        userBasicInfo = new UserBasicInfo(123L, "User", "Test", userEmail, 1000.0, "Cédula", "CLIENTE");
        userEmails = List.of(userEmail);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("Should return UserBasicInfo when user exists")
    void shouldReturnUserBasicInfoWhenUserExists() {
        // Arrange
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(UserBasicInfo.class)).thenReturn(Mono.just(userBasicInfo));

        // Act & Assert
        StepVerifier.create(restConsumer.getUserByEmail(userEmail))
            .expectNext(userBasicInfo)
            .verifyComplete();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @DisplayName("Should handle ServiceNotAvailabeException in getUserByEmail")
    void shouldHandleServiceNotAvailabeExceptionInGetUserByEmail() {
        // Arrange
        RuntimeException simulatedException = new RuntimeException("Simulated service unavailable");
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(UserBasicInfo.class)).thenReturn(Mono.error(simulatedException));

        // Act & Assert
        StepVerifier.create(restConsumer.getUserByEmail(userEmail))
            .expectError(ServiceNotAvailabeException.class)
            .verify();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("Should return UserBasicInfo list when getUsersBasicInfo is called")
    void shouldReturnUserBasicInfoListWhenGetUsersBasicInfoIsCalled() {
        // Arrange
        RequestBodyUriSpec requestBodyUriSpecMock = mock(RequestBodyUriSpec.class);
        RequestHeadersSpec requestHeadersSpecMock = mock(RequestHeadersSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.bodyValue(anyList())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(eq(HttpHeaders.CONTENT_TYPE), eq(MediaType.APPLICATION_JSON_VALUE)))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(UserBasicInfo.class)).thenReturn(Flux.just(userBasicInfo));

        // Act & Assert
        StepVerifier.create(restConsumer.getUsersBasicInfo(userEmails))
            .expectNext(userBasicInfo)
            .verifyComplete();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @DisplayName("Should handle ServiceNotAvailabeException in getUsersBasicInfo")
    void shouldHandleServiceNotAvailabeExceptionInGetUsersBasicInfo() {
        // Arrange
        RuntimeException simulatedException = new RuntimeException("Simulated service unavailable");
        RequestBodyUriSpec requestBodyUriSpecMock = mock(RequestBodyUriSpec.class);
        RequestHeadersSpec requestHeadersSpecMock = mock(RequestHeadersSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.bodyValue(anyList())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(eq(HttpHeaders.CONTENT_TYPE), eq(MediaType.APPLICATION_JSON_VALUE)))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToFlux(UserBasicInfo.class)).thenReturn(Flux.error(simulatedException));

        // Act & Assert
        StepVerifier.create(restConsumer.getUsersBasicInfo(userEmails))
            .expectError(ServiceNotAvailabeException.class)
            .verify();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("Should return UserBasicInfo when getRequestUserByToken is called")
    void shouldReturnUserBasicInfoWhenGetRequestUserByTokenIsCalled() {
        // Arrange
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(UserBasicInfo.class)).thenReturn(Mono.just(userBasicInfo));

        // Act & Assert
        StepVerifier.create(restConsumer.getRequestUserByToken())
            .expectNext(userBasicInfo)
            .verifyComplete();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("Should return UserBasicInfo when getUserByIdNumber is called")
    void shouldReturnUserBasicInfoWhenGetUserByIdNumberIsCalled() {
        // Arrange
        Long idNumber = 123L;
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(UserBasicInfo.class)).thenReturn(Mono.just(userBasicInfo));

        // Act & Assert
        StepVerifier.create(restConsumer.getUserByIdNumber(idNumber))
            .expectNext(userBasicInfo)
            .verifyComplete();
    }
}