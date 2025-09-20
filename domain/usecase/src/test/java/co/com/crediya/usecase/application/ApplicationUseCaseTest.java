package co.com.crediya.usecase.application;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.PageResult;
import co.com.crediya.model.application.criteria.SearchCriteria;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.NotificationsSQSGateway;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.application.record.ApplicationRecord;
import co.com.crediya.model.application.record.ApplicationWithUserInfoRecord;
import co.com.crediya.model.application.record.UserBasicInfo;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private NotificationsSQSGateway notificationsSQSGateway;

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    private LoanType loanType1;
    private LoanStatus loanStatus1;
    private Application application1;
    private Application application2;
    private Application application3;
    private ApplicationRecord applicationRecord1;
    private ApplicationRecord applicationRecord2;
    private ApplicationRecord applicationRecord3;
    private UserBasicInfo userBasicInfo1;
    private PageResult<Application> pageResult;

    @BeforeEach
    void setUp() {
        loanType1 = LoanType.builder().loanTypeId(1).name("Personal Loan").interestRate(5.0).build();
        loanStatus1 = LoanStatus.builder().loanStatusId(1).name("Pending").build();
        UUID applicationId1 = UUID.randomUUID();
        UUID applicationId2 = UUID.randomUUID();
        UUID applicationId3 = UUID.randomUUID();

        application1 = Application.builder()
                .applicationId(applicationId1)
                .userEmail("test1@example.com")
                .loanAmount(10000.0)
                .loanTerm(36.0)
                .loanTypeId(1)
                .loanStatusId(1)
                .build();

        application2 = Application.builder()
                .applicationId(applicationId2)
                .userEmail("test2@example.com")
                .loanAmount(20000.0)
                .loanTerm(60.0)
                .loanTypeId(1)
                .loanStatusId(1)
                .build();

        application3 = Application.builder()
                .applicationId(applicationId3)
                .userIdNumber(123456789L)
                .userEmail("test1@example.com")
                .loanAmount(20000.0)
                .loanTerm(60.0)
                .loanTypeId(1)
                .loanStatusId(1)
                .build();

        applicationRecord1 = new ApplicationRecord(
                applicationId1,
                "test1@example.com",
                10000.0,
                36.0,
                loanType1,
                loanStatus1);
        applicationRecord2 = new ApplicationRecord(
                applicationId2,
                "test2@example.com",
                20000.0,
                60.0,
                loanType1,
                loanStatus1);
        applicationRecord3 = new ApplicationRecord(
                applicationId3,
                "test1@example.com",
                20000.0,
                60.0,
                loanType1,
                loanStatus1);

        userBasicInfo1 = new UserBasicInfo(123456789L, "John", "Doe", "test1@example.com", 50000.0);
    
        pageResult = new PageResult<>(List.of(application1), 1L, 0, 10);
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
    @DisplayName("Should get applications by userEmail successfully")
    void shouldGetApplicationsByUserEmailSuccessfully() {
        // Arrange
        String userEmail = "test1@example.com";
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1));
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1));
        when(applicationRepository.getApplicationsByUserEmail(userEmail)).thenReturn(Flux.just(application1));

        // Act & Assert
        StepVerifier.create(applicationUseCase.getApplicationsByUserEmail(userEmail))
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
        when(userGateway.getUserByIdNumber(anyLong())).thenReturn(Mono.just(userBasicInfo1));
        when(loanStatusRepository.getLoanStatusById(anyInt())).thenReturn(Mono.just(loanStatus1));
        when(applicationRepository.saveApplication(any(Mono.class))).thenReturn(Mono.just(application3));
        when(userGateway.getRequestUserByToken()).thenReturn(Mono.just(userBasicInfo1));

        // Act
        Mono<Application> applicationMono = Mono.just(application3);
        // Act & Assert
        StepVerifier.create(applicationUseCase.saveApplication(applicationMono))
                .expectNext(applicationRecord3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when user does not exist")
    void shouldReturnErrorWhenUserDoesNotExist() {
        // Arrange
        when(loanTypeRepository.getLoanTypeById(anyInt())).thenReturn(Mono.just(loanType1));
        when(userGateway.getUserByIdNumber(anyLong())).thenReturn(Mono.empty());
        when(loanStatusRepository.getLoanStatusById(anyInt())).thenReturn(Mono.just(loanStatus1));
        when(userGateway.getRequestUserByToken()).thenReturn(Mono.just(userBasicInfo1));
        
        // Act
        Mono<Application> applicationMono = Mono.just(application3);

        // Act & Assert
        StepVerifier.create(applicationUseCase.saveApplication(applicationMono))
            .expectError(InvalidDataException.class)
            .verify();
    }

    @Test
        @DisplayName("Should get paginated applications by criteria successfully")
        void shouldGetByCriteriaPaginatedSuccessfully() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("email")
                .sortDirection("asc")
                .filters(new HashMap<>())
                .build();

        List<String> expectedEmails = List.of("test1@example.com");

        when(applicationRepository.findByCriteria(any(SearchCriteria.class))).thenReturn(Mono.just(pageResult));
        when(loanTypeRepository.getAllLoanTypes()).thenReturn(Flux.just(loanType1));
        when(loanStatusRepository.getAllLoanStatuses()).thenReturn(Flux.just(loanStatus1));
        when(userGateway.getUsersBasicInfo(expectedEmails)).thenReturn(Flux.just(userBasicInfo1));

        // Act & Assert
        StepVerifier.create(applicationUseCase.getByCriteriaPaginated(criteria))
                .expectNextMatches(actualPageResult -> {
                        assertEquals(pageResult.getTotalElements(), actualPageResult.getTotalElements());
                        assertEquals(pageResult.getCurrentPage(), actualPageResult.getCurrentPage());
                        assertEquals(pageResult.getSize(), actualPageResult.getSize());
                        List<ApplicationWithUserInfoRecord> expectedContent = actualPageResult.getContent();
                        assertEquals(expectedContent.get(0).applicationId(), application1.getApplicationId());
                        assertEquals(expectedContent.get(0).email(), application1.getUserEmail());
                        return true;
                })
                .verifyComplete();
        }
}