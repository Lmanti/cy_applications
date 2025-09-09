package co.com.crediya.model.loantype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {

    @Test
    @DisplayName("Should create LoanType with no-args constructor")
    void shouldCreateLoanTypeWithNoArgsConstructor() {
        // When
        LoanType loanType = new LoanType();

        // Then
        assertNotNull(loanType);
        assertNull(loanType.getLoanTypeId());
        assertNull(loanType.getName());
        assertNull(loanType.getMinAmount());
        assertNull(loanType.getMaxAmount());
        assertNull(loanType.getInterestRate());
        assertNull(loanType.getAutoValidation());
    }

    @Test
    @DisplayName("Should create LoanType with all-args constructor")
    void shouldCreateLoanTypeWithAllArgsConstructor() {
        // Given
        Integer loanTypeId = 1;
        String name = "Personal Loan";
        Double minAmount = 1000.0;
        Double maxAmount = 50000.0;
        Double interestRate = 0.10;
        Boolean autoValidation = true;

        // When
        LoanType loanType = new LoanType(loanTypeId, name, minAmount, maxAmount, interestRate, autoValidation);

        // Then
        assertEquals(loanTypeId, loanType.getLoanTypeId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertEquals(maxAmount, loanType.getMaxAmount());
        assertEquals(interestRate, loanType.getInterestRate());
        assertEquals(autoValidation, loanType.getAutoValidation());
    }

    @Test
    @DisplayName("Should create LoanType with builder")
    void shouldCreateLoanTypeWithBuilder() {
        // Given
        Integer loanTypeId = 2;
        String name = "Car Loan";
        Double minAmount = 5000.0;

        // When
        LoanType loanType = LoanType.builder()
                .loanTypeId(loanTypeId)
                .name(name)
                .minAmount(minAmount)
                .build();

        // Then
        assertEquals(loanTypeId, loanType.getLoanTypeId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertNull(loanType.getMaxAmount());
        assertNull(loanType.getInterestRate());
        assertNull(loanType.getAutoValidation());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        LoanType original = LoanType.builder()
                .loanTypeId(1)
                .name("Original Loan")
                .minAmount(1000.0)
                .maxAmount(10000.0)
                .interestRate(0.05)
                .autoValidation(false)
                .build();

        // When
        LoanType copy = original.toBuilder()
                .name("Modified Loan")
                .interestRate(0.06)
                .build();

        // Then
        assertEquals(original.getLoanTypeId(), copy.getLoanTypeId());
        assertEquals("Modified Loan", copy.getName());
        assertEquals(original.getMinAmount(), copy.getMinAmount());
        assertEquals(original.getMaxAmount(), copy.getMaxAmount());
        assertEquals(0.06, copy.getInterestRate());
        assertEquals(original.getAutoValidation(), copy.getAutoValidation());
        assertEquals("Original Loan", original.getName());
        assertEquals(0.05, original.getInterestRate());
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        // Given
        LoanType loanType = new LoanType();
        Integer loanTypeId = 3;
        String name = "Mortgage";
        Double minAmount = 100000.0;
        Double interestRate = 0.03;

        // When
        loanType.setLoanTypeId(loanTypeId);
        loanType.setName(name);
        loanType.setMinAmount(minAmount);
        loanType.setInterestRate(interestRate);

        // Then
        assertEquals(loanTypeId, loanType.getLoanTypeId());
        assertEquals(name, loanType.getName());
        assertEquals(minAmount, loanType.getMinAmount());
        assertEquals(interestRate, loanType.getInterestRate());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        LoanType loanType = LoanType.builder()
                .loanTypeId(1)
                .name("Initial Loan")
                .minAmount(1000.0)
                .maxAmount(10000.0)
                .interestRate(0.05)
                .autoValidation(false)
                .build();

        // When
        loanType.setLoanTypeId(null);
        loanType.setName(null);
        loanType.setMinAmount(null);
        loanType.setMaxAmount(null);
        loanType.setInterestRate(null);
        loanType.setAutoValidation(null);

        // Then
        assertNull(loanType.getLoanTypeId());
        assertNull(loanType.getName());
        assertNull(loanType.getMinAmount());
        assertNull(loanType.getMaxAmount());
        assertNull(loanType.getInterestRate());
        assertNull(loanType.getAutoValidation());
    }

    @Test
    @DisplayName("Should create LoanType and verify with StepVerifier")
    void shouldCreateLoanTypeAndVerifyWithStepVerifier() {
        // Given
        Integer loanTypeId = 1;
        String name = "Personal Loan";
        Double minAmount = 1000.0;
        Double maxAmount = 50000.0;
        Double interestRate = 0.10;
        Boolean autoValidation = true;

        // When
        LoanType loanType = new LoanType(loanTypeId, name, minAmount, maxAmount, interestRate, autoValidation);

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(loanType))
                .assertNext(type -> {
                    assertEquals(loanTypeId, type.getLoanTypeId());
                    assertEquals(name, type.getName());
                    assertEquals(minAmount, type.getMinAmount());
                    assertEquals(maxAmount, type.getMaxAmount());
                    assertEquals(interestRate, type.getInterestRate());
                    assertEquals(autoValidation, type.getAutoValidation());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should build LoanType and verify with StepVerifier")
    void shouldBuildLoanTypeAndVerifyWithStepVerifier() {
        // Given
        Integer loanTypeId = 2;
        String name = "Car Loan";
        Double minAmount = 5000.0;

        // When
        LoanType loanType = LoanType.builder()
                .loanTypeId(loanTypeId)
                .name(name)
                .minAmount(minAmount)
                .build();

        // Then
        StepVerifier.create(reactor.core.publisher.Mono.just(loanType))
                .assertNext(type -> {
                    assertEquals(loanTypeId, type.getLoanTypeId());
                    assertEquals(name, type.getName());
                    assertEquals(minAmount, type.getMinAmount());
                    assertNull(type.getMaxAmount());
                    assertNull(type.getInterestRate());
                    assertNull(type.getAutoValidation());
                })
                .verifyComplete();
    }
}
