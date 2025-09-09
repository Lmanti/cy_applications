package co.com.crediya.model.loanstatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class LoanStatusTest {

    @Test
    @DisplayName("Should create LoanStatus with no-args constructor")
    void shouldCreateLoanStatusWithNoArgsConstructor() {
        // When
        LoanStatus loanStatus = new LoanStatus();

        // Then
        assertNotNull(loanStatus);
        assertNull(loanStatus.getLoanStatusId());
        assertNull(loanStatus.getName());
        assertNull(loanStatus.getDescription());
    }

    @Test
    @DisplayName("Should create LoanStatus with all-args constructor")
    void shouldCreateLoanStatusWithAllArgsConstructor() {
        // Given
        Integer loanStatusId = 1;
        String name = "Pending";
        String description = "Application is awaiting approval";

        // When
        LoanStatus loanStatus = new LoanStatus(loanStatusId, name, description);

        // Then
        assertEquals(loanStatusId, loanStatus.getLoanStatusId());
        assertEquals(name, loanStatus.getName());
        assertEquals(description, loanStatus.getDescription());
    }

    @Test
    @DisplayName("Should create LoanStatus with builder")
    void shouldCreateLoanStatusWithBuilder() {
        // Given
        Integer loanStatusId = 2;
        String name = "Approved";

        // When
        LoanStatus loanStatus = LoanStatus.builder()
                .loanStatusId(loanStatusId)
                .name(name)
                .build();

        // Then
        assertEquals(loanStatusId, loanStatus.getLoanStatusId());
        assertEquals(name, loanStatus.getName());
        assertNull(loanStatus.getDescription());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        LoanStatus original = LoanStatus.builder()
                .loanStatusId(1)
                .name("Original Status")
                .description("Original Description")
                .build();

        // When
        LoanStatus copy = original.toBuilder()
                .name("Modified Status")
                .description("Modified Description")
                .build();

        // Then
        assertEquals(original.getLoanStatusId(), copy.getLoanStatusId());
        assertEquals("Modified Status", copy.getName());
        assertEquals("Modified Description", copy.getDescription());
        assertEquals("Original Status", original.getName());
        assertEquals("Original Description", original.getDescription());
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        // Given
        LoanStatus loanStatus = new LoanStatus();
        Integer loanStatusId = 3;
        String name = "Rejected";
        String description = "Application was rejected";

        // When
        loanStatus.setLoanStatusId(loanStatusId);
        loanStatus.setName(name);
        loanStatus.setDescription(description);

        // Then
        assertEquals(loanStatusId, loanStatus.getLoanStatusId());
        assertEquals(name, loanStatus.getName());
        assertEquals(description, loanStatus.getDescription());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        LoanStatus loanStatus = LoanStatus.builder()
                .loanStatusId(1)
                .name("Initial Status")
                .description("Initial Description")
                .build();

        // When
        loanStatus.setLoanStatusId(null);
        loanStatus.setName(null);
        loanStatus.setDescription(null);

        // Then
        assertNull(loanStatus.getLoanStatusId());
        assertNull(loanStatus.getName());
        assertNull(loanStatus.getDescription());
    }

    @Test
    @DisplayName("Should create LoanStatus and verify with StepVerifier")
    void shouldCreateLoanStatusAndVerifyWithStepVerifier() {
        // Given
        Integer loanStatusId = 1;
        String name = "Pending";
        String description = "Application is awaiting approval";

        // When
        LoanStatus loanStatus = new LoanStatus(loanStatusId, name, description);

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(loanStatus))
                .assertNext(status -> {
                    assertEquals(loanStatusId, status.getLoanStatusId());
                    assertEquals(name, status.getName());
                    assertEquals(description, status.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should build LoanStatus and verify with StepVerifier")
    void shouldBuildLoanStatusAndVerifyWithStepVerifier() {
        // Given
        Integer loanStatusId = 2;
        String name = "Approved";

        // When
        LoanStatus loanStatus = LoanStatus.builder()
                .loanStatusId(loanStatusId)
                .name(name)
                .build();

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(loanStatus))
                .assertNext(status -> {
                    assertEquals(loanStatusId, status.getLoanStatusId());
                    assertEquals(name, status.getName());
                    assertNull(status.getDescription());
                })
                .verifyComplete();
    }
}