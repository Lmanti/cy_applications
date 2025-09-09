package co.com.crediya.consumer;

import co.com.crediya.model.application.exception.ServiceNotAvailabeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    @Mock
    private WebClient client;

    @InjectMocks
    private RestConsumer restConsumer;

    private Long userIdNumber;

    @BeforeEach
    void setUp() {
        userIdNumber = 123456789L;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("Should return true when user exists")
    void shouldReturnTrueWhenUserExists() {
        // Arrange
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);  // Capture the URI
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(restConsumer.existByIdNumber(userIdNumber))
            .expectNext(true)
            .verifyComplete();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @DisplayName("Should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        // Arrange
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);  // Capture the URI
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(restConsumer.existByIdNumber(userIdNumber))
            .expectNext(false)
            .verifyComplete();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @DisplayName("Should handle ServiceNotAvailabeException in fallback")
    void shouldHandleServiceNotAvailabeExceptionInFallback() {
        // Arrange
        RuntimeException simulatedException = new RuntimeException("Simulated service unavailable");
        RequestHeadersUriSpec requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Boolean.class)).thenReturn(Mono.error(simulatedException));

        // Act & Assert
        StepVerifier.create(restConsumer.existByIdNumber(userIdNumber))
            .expectError(ServiceNotAvailabeException.class)
            .verify();
    }
}