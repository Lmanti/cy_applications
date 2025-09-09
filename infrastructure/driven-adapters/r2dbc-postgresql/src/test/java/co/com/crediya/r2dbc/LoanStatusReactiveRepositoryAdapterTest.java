package co.com.crediya.r2dbc;

import co.com.crediya.model.application.exception.DataRetrievalException;
import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.r2dbc.entity.LoanStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStatusReactiveRepositoryAdapterTest {

    @Mock
    private LoanStatusReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private LoanStatusReactiveRepositoryAdapter adapter;

    private LoanStatus loanStatus1;
    private LoanStatus loanStatus2;
    private LoanStatusEntity loanStatusEntity1;
    private LoanStatusEntity loanStatusEntity2;

    @BeforeEach
    void setUp() {
        loanStatus1 = LoanStatus.builder().loanStatusId(1).name("Pending").description("Application is pending").build();
        loanStatus2 = LoanStatus.builder().loanStatusId(2).name("Approved").description("Application is approved").build();

        loanStatusEntity1 = new LoanStatusEntity(1, "Pending", "Application is pending");
        loanStatusEntity2 = new LoanStatusEntity(2, "Approved", "Application is approved");


    }

    @Test
    @DisplayName("Should get all loan statuses successfully")
    void shouldGetAllLoanStatusesSuccessfully() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(loanStatusEntity1, loanStatusEntity2));
        when(mapper.map(loanStatusEntity1, LoanStatus.class)).thenReturn(loanStatus1);
        when(mapper.map(loanStatusEntity2, LoanStatus.class)).thenReturn(loanStatus2);

        // Act & Assert
        StepVerifier.create(adapter.getAllLoanStatuses())
            .expectNext(loanStatus1, loanStatus2)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getAllLoanStatuses fails")
    void shouldReturnDataRetrievalExceptionWhenGetAllLoanStatusesFails() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getAllLoanStatuses())
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should get loan status by id successfully")
    void shouldGetLoanStatusByIdSuccessfully() {
        // Arrange
        int loanStatusId = 1;
        when(repository.findById(loanStatusId)).thenReturn(Mono.just(loanStatusEntity1));
        when(mapper.map(loanStatusEntity1, LoanStatus.class)).thenReturn(loanStatus1);

        // Act & Assert
        StepVerifier.create(adapter.getLoanStatusById(loanStatusId))
            .expectNext(loanStatus1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getLoanStatusById fails")
    void shouldReturnDataRetrievalExceptionWhenGetLoanStatusByIdFails() {
        // Arrange
        int loanStatusId = 1;
        when(repository.findById(loanStatusId)).thenReturn(Mono.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getLoanStatusById(loanStatusId))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when loan status is not found")
    void shouldReturnDataRetrievalExceptionWhenLoanStatusIsNotFound() {
        // Arrange
        int loanStatusId = 1;
        when(repository.findById(loanStatusId)).thenReturn(Mono.empty()); // Simulate not found

        // Act & Assert
        StepVerifier.create(adapter.getLoanStatusById(loanStatusId))
            .expectError(DataRetrievalException.class)
            .verify();
    }
}
