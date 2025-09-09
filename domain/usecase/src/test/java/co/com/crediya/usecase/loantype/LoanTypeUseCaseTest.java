package co.com.crediya.usecase.loantype;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeUseCaseTest {

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @InjectMocks
    private LoanTypeUseCase loanTypeUseCase;

    private LoanType loanType1;
    private LoanType loanType2;

    @BeforeEach
    void setUp() {
        loanType1 = LoanType.builder().loanTypeId(1).name("Personal Loan").minAmount(1000.0).build();
        loanType2 = LoanType.builder().loanTypeId(2).name("Car Loan").maxAmount(50000.0).build();
    }

    @Test
    @DisplayName("Should get all loan types successfully")
    void shouldGetAllLoanTypesSuccessfully() {
        // Arrange
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1, loanType2));

        // Act & Assert
        StepVerifier.create(loanTypeUseCase.getAllLoanTypes())
                .expectNext(loanType1, loanType2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get loan type by id successfully")
    void shouldGetLoanTypeByIdSuccessfully() {
        // Arrange
        int loanTypeId = 1;
        when(loanTypeRepository.getLoanTypeById(loanTypeId)).thenReturn(Mono.just(loanType1));

        // Act & Assert
        StepVerifier.create(loanTypeUseCase.getLoanTypeById(loanTypeId))
                .expectNext(loanType1)
                .verifyComplete();
    }
}
