package co.com.crediya.model.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    @DisplayName("Should create Application with no-args constructor")
    void shouldCreateApplicationWithNoArgsConstructor() {
        // When
        Application application = new Application();

        // Then
        assertNotNull(application);
        assertNull(application.getApplicationId());
        assertNull(application.getUserIdNumber());
        assertNull(application.getLoanAmount());
        assertNull(application.getLoanTerm());
        assertNull(application.getLoanTypeId());
        assertNull(application.getLoanStatusId());
    }

    @Test
    @DisplayName("Should create Application with all-args constructor")
    void shouldCreateApplicationWithAllArgsConstructor() {
        // Given
        UUID applicationId = UUID.randomUUID();
        Long userIdNumber = 123456789L;
        Double loanAmount = 10000.0;
        Double loanTerm = 36.0;
        Integer loanTypeId = 1;
        Integer loanStatusId = 2;

        // When
        Application application = new Application(applicationId, userIdNumber, loanAmount, loanTerm, loanTypeId, loanStatusId);

        // Then
        assertEquals(applicationId, application.getApplicationId());
        assertEquals(userIdNumber, application.getUserIdNumber());
        assertEquals(loanAmount, application.getLoanAmount());
        assertEquals(loanTerm, application.getLoanTerm());
        assertEquals(loanTypeId, application.getLoanTypeId());
        assertEquals(loanStatusId, application.getLoanStatusId());
    }

    @Test
    @DisplayName("Should create Application with builder")
    void shouldCreateApplicationWithBuilder() {
        // Given
        UUID applicationId = UUID.randomUUID();
        Long userIdNumber = 987654321L;
        Double loanAmount = 20000.0;

        // When
        Application application = Application.builder()
                .applicationId(applicationId)
                .userIdNumber(userIdNumber)
                .loanAmount(loanAmount)
                .build();

        // Then
        assertEquals(applicationId, application.getApplicationId());
        assertEquals(userIdNumber, application.getUserIdNumber());
        assertEquals(loanAmount, application.getLoanAmount());
        assertNull(application.getLoanTerm());
        assertNull(application.getLoanTypeId());
        assertNull(application.getLoanStatusId());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        Application original = Application.builder()
                .applicationId(UUID.randomUUID())
                .userIdNumber(111222333L)
                .loanAmount(5000.0)
                .build();

        // When
        Application copy = original.toBuilder()
                .loanAmount(6000.0)
                .loanTerm(24.0)
                .build();

        // Then
        assertEquals(original.getApplicationId(), copy.getApplicationId());
        assertEquals(original.getUserIdNumber(), copy.getUserIdNumber());
        assertEquals(6000.0, copy.getLoanAmount());
        assertEquals(24.0, copy.getLoanTerm());
        assertEquals(5000.0, original.getLoanAmount());
        assertNull(copy.getLoanTypeId());
        assertNull(copy.getLoanStatusId());
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        // Given
        Application application = new Application();
        UUID applicationId = UUID.randomUUID();
        Long userIdNumber = 555666777L;
        Double loanAmount = 7500.0;

        // When
        application.setApplicationId(applicationId);
        application.setUserIdNumber(userIdNumber);
        application.setLoanAmount(loanAmount);

        // Then
        assertEquals(applicationId, application.getApplicationId());
        assertEquals(userIdNumber, application.getUserIdNumber());
        assertEquals(loanAmount, application.getLoanAmount());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        Application application = Application.builder()
                .applicationId(UUID.randomUUID())
                .userIdNumber(123456789L)
                .loanAmount(10000.0)
                .build();

        // When
        application.setApplicationId(null);
        application.setUserIdNumber(null);
        application.setLoanAmount(null);

        // Then
        assertNull(application.getApplicationId());
        assertNull(application.getUserIdNumber());
        assertNull(application.getLoanAmount());
    }

    @Test
    @DisplayName("Should create Application and verify with StepVerifier")
    void shouldCreateApplicationAndVerifyWithStepVerifier() {
        // Given
        UUID applicationId = UUID.randomUUID();
        Long userIdNumber = 123456789L;
        Double loanAmount = 10000.0;
        Double loanTerm = 36.0;
        Integer loanTypeId = 1;
        Integer loanStatusId = 2;

        // When
        Application application = new Application(applicationId, userIdNumber, loanAmount, loanTerm, loanTypeId, loanStatusId);

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(application))
                .assertNext(app -> {
                    assertEquals(applicationId, app.getApplicationId());
                    assertEquals(userIdNumber, app.getUserIdNumber());
                    assertEquals(loanAmount, app.getLoanAmount());
                    assertEquals(loanTerm, app.getLoanTerm());
                    assertEquals(loanTypeId, app.getLoanTypeId());
                    assertEquals(loanStatusId, app.getLoanStatusId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should build Application and verify with StepVerifier")
    void shouldBuildApplicationAndVerifyWithStepVerifier() {
        // Given
        UUID applicationId = UUID.randomUUID();
        Long userIdNumber = 987654321L;
        Double loanAmount = 20000.0;

        // When
        Application application = Application.builder()
                .applicationId(applicationId)
                .userIdNumber(userIdNumber)
                .loanAmount(loanAmount)
                .build();

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(application))
                .assertNext(app -> {
                    assertEquals(applicationId, app.getApplicationId());
                    assertEquals(userIdNumber, app.getUserIdNumber());
                    assertEquals(loanAmount, app.getLoanAmount());
                    assertNull(app.getLoanTerm());
                    assertNull(app.getLoanTypeId());
                    assertNull(app.getLoanStatusId());
                })
                .verifyComplete();
    }
}
