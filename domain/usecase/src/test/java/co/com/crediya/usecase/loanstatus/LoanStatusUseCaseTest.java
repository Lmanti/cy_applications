package co.com.crediya.usecase.loanstatus;

import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loanstatus.gateways.LoanStatusRepository;
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
class LoanStatusUseCaseTest {

    @Mock
    private LoanStatusRepository loanStatusRepository;

    @InjectMocks
    private LoanStatusUseCase loanStatusUseCase;

    private LoanStatus loanStatus1;
    private LoanStatus loanStatus2;

    @BeforeEach
    void setUp() {
        loanStatus1 = LoanStatus.builder().loanStatusId(1).name("Pending").description("Application is pending").build();
        loanStatus2 = LoanStatus.builder().loanStatusId(2).name("Approved").description("Application is approved").build();
    }

    @Test
    @DisplayName("Should get all loan statuses successfully")
    void shouldGetAllLoanStatusesSuccessfully() {
        // Arrange
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1, loanStatus2));

        // Act & Assert
        StepVerifier.create(loanStatusUseCase.getAllLoanStatuses())
                .expectNext(loanStatus1, loanStatus2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get loan status by id successfully")
    void shouldGetLoanStatusByIdSuccessfully() {
        // Arrange
        int loanStatusId = 1;
        when(loanStatusRepository.getLoanStatusById(loanStatusId)).thenReturn(Mono.just(loanStatus1));

        // Act & Assert
        StepVerifier.create(loanStatusUseCase.getLoanStatusById(loanStatusId))
                .expectNext(loanStatus1)
                .verifyComplete();
    }
}
