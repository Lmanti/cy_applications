package co.com.crediya.usecase.application;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exception.DataRetrievalException;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.application.record.ApplicationRecord;
import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loanstatus.gateways.LoanStatusRepository;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private LoanStatusRepository loanStatusRepository;
    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    private LoanType loanType1;
    private LoanStatus loanStatus1;
    private Application application1;
    private Application application2;
    private ApplicationRecord applicationRecord1;
    private ApplicationRecord applicationRecord2;

    @BeforeEach
    void setUp() {
        loanType1 = LoanType.builder().loanTypeId(1).name("Personal Loan").build();
        loanStatus1 = LoanStatus.builder().loanStatusId(1).name("Pending").build();
        UUID applicationId1 = UUID.randomUUID();
        UUID applicationId2 = UUID.randomUUID();

        application1 = Application.builder()
                .applicationId(applicationId1)
                .userIdNumber(123456789L)
                .loanAmount(10000.0)
                .loanTerm(36.0)
                .loanTypeId(1)
                .loanStatusId(1)
                .build();

        application2 = Application.builder()
                .applicationId(applicationId2)
                .userIdNumber(987654321L)
                .loanAmount(20000.0)
                .loanTerm(60.0)
                .loanTypeId(1)
                .loanStatusId(1)
                .build();

        applicationRecord1 = new ApplicationRecord(
                applicationId1,
                123456789L,
                10000.0,
                36.0,
                loanType1,
                loanStatus1);
        applicationRecord2 = new ApplicationRecord(
                applicationId2,
                987654321L,
                20000.0,
                60.0,
                loanType1,
                loanStatus1);
    }

    @Test
    @DisplayName("Should get all applications successfully")
    void shouldGetAllApplicationsSuccessfully() {
        // Arrange
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1));
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1));
        when(applicationRepository.getAllApplications()).thenReturn(Flux.just(application1, application2));

        // Act & Assert
        StepVerifier.create(applicationUseCase.getAllApplications())
                .expectNext(applicationRecord1, applicationRecord2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get applications by userIdNumber successfully")
    void shouldGetApplicationsByUserIdNumberSuccessfully() {
        // Arrange
        Long userIdNumber = 123456789L;
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1));
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1));
        when(applicationRepository.getApplicationsByUserIdNumber(userIdNumber)).thenReturn(Flux.just(application1));

        // Act & Assert
        StepVerifier.create(applicationUseCase.getApplicationsByUserIdNumber(userIdNumber))
                .expectNext(applicationRecord1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get application by applicationId successfully")
    void shouldGetApplicationByApplicationIdSuccessfully() {
        // Arrange
        UUID applicationId = application1.getApplicationId();
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1));
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1));
        when(applicationRepository.getApplicationsByApplicationId(applicationId)).thenReturn(Mono.just(application1));

        // Act & Assert
        StepVerifier.create(applicationUseCase.getApplicationsByApplicationId(applicationId))
                .expectNext(applicationRecord1)
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should save application successfully")
    void shouldSaveApplicationSuccessfully() {
        // Arrange
        when(loanTypeRepository.getLoanTypeById(anyInt())).thenReturn(Mono.just(loanType1));
        when(userGateway.existByIdNumber(anyLong())).thenReturn(Mono.just(true));
        when(loanStatusRepository.getLoanStatusById(anyInt())).thenReturn(Mono.just(loanStatus1));
        when(applicationRepository.saveApplication(any(Mono.class))).thenReturn(Mono.just(application1));

        // Act
        Mono<Application> applicationMono = Mono.just(application1);
        // Act & Assert
        StepVerifier.create(applicationUseCase.saveApplication(applicationMono))
                .expectNext(applicationRecord1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when user does not exist")
    void shouldReturnErrorWhenUserDoesNotExist() {
        // Arrange
        when(loanTypeRepository.getLoanTypeById(anyInt())).thenReturn(Mono.just(loanType1));
        when(userGateway.existByIdNumber(anyLong())).thenReturn(Mono.just(false));
        when(loanStatusRepository.getLoanStatusById(anyInt())).thenReturn(Mono.just(loanStatus1));

        // Act
        Mono<Application> applicationMono = Mono.just(application1);

        // Act & Assert
        StepVerifier.create(applicationUseCase.saveApplication(applicationMono))
            .expectError(InvalidDataException.class)
            .verify();
    }

    @Test
    @DisplayName("Should return error when LoanStatus does not exist")
    void shouldReturnErrorWhenLoanStatusDoesNotExist() {
        // Arrange
        when(loanTypeRepository.getLoanTypeById(anyInt())).thenReturn(Mono.just(loanType1));
        when(userGateway.existByIdNumber(anyLong())).thenReturn(Mono.just(true));
        when(loanStatusRepository.getLoanStatusById(anyInt())).thenReturn(Mono.error(new DataRetrievalException("Estado de crédito con ID 1 no encontrado")));

        // Act
        Mono<Application> applicationMono = Mono.just(application1);

        // Act & Assert
        StepVerifier.create(applicationUseCase.saveApplication(applicationMono))
            .expectError(DataRetrievalException.class)
            .verify();
    }
}
