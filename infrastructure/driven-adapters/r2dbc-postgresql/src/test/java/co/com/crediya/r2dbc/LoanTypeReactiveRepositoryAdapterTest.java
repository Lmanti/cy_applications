package co.com.crediya.r2dbc;

import co.com.crediya.model.application.exception.DataRetrievalException;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
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
class LoanTypeReactiveRepositoryAdapterTest {

    @Mock
    private LoanTypeReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private LoanTypeReactiveRepositoryAdapter adapter;

    private LoanType loanType1;
    private LoanType loanType2;
    private LoanTypeEntity loanTypeEntity1;
    private LoanTypeEntity loanTypeEntity2;

    @BeforeEach
    void setUp() {
        loanType1 = LoanType.builder().loanTypeId(1).name("Personal Loan").minAmount(1000.0).build();
        loanType2 = LoanType.builder().loanTypeId(2).name("Car Loan").maxAmount(50000.0).build();

        loanTypeEntity1 = new LoanTypeEntity(1, "Personal Loan", 1000.0, null, null, null);
        loanTypeEntity2 = new LoanTypeEntity(2, "Car Loan", null, 50000.0, null, null);


    }

    @Test
    @DisplayName("Should get all loan types successfully")
    void shouldGetAllLoanTypesSuccessfully() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(loanTypeEntity1, loanTypeEntity2));
        when(mapper.map(loanTypeEntity1, LoanType.class)).thenReturn(loanType1);
        when(mapper.map(loanTypeEntity2, LoanType.class)).thenReturn(loanType2);

        // Act & Assert
        StepVerifier.create(adapter.getAllLoanTypes())
            .expectNext(loanType1, loanType2)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getAllLoanTypes fails")
    void shouldReturnDataRetrievalExceptionWhenGetAllLoanTypesFails() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getAllLoanTypes())
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should get loan type by id successfully")
    void shouldGetLoanTypeByIdSuccessfully() {
        // Arrange
        int loanTypeId = 1;
        when(repository.findById(loanTypeId)).thenReturn(Mono.just(loanTypeEntity1));
        when(mapper.map(loanTypeEntity1, LoanType.class)).thenReturn(loanType1);

        // Act & Assert
        StepVerifier.create(adapter.getLoanTypeById(loanTypeId))
            .expectNext(loanType1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getLoanTypeById fails")
    void shouldReturnDataRetrievalExceptionWhenGetLoanTypeByIdFails() {
        // Arrange
        int loanTypeId = 1;
        when(repository.findById(loanTypeId)).thenReturn(Mono.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getLoanTypeById(loanTypeId))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when loan type is not found")
    void shouldReturnDataRetrievalExceptionWhenLoanTypeIsNotFound() {
        // Arrange
        int loanTypeId = 1;
        when(repository.findById(loanTypeId)).thenReturn(Mono.empty()); // Simulate not found

        // Act & Assert
        StepVerifier.create(adapter.getLoanTypeById(loanTypeId))
            .expectError(DataRetrievalException.class)
            .verify();
    }
}
