package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exception.DataPersistenceException;
import co.com.crediya.model.application.exception.DataRetrievalException;
import co.com.crediya.r2dbc.entity.ApplicationEntity;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationReactiveRepositoryAdapterTest {

    @Mock
    private ApplicationReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ApplicationReactiveRepositoryAdapter adapter;

    private Application application1;
    private Application application2;
    private ApplicationEntity applicationEntity1;
    private ApplicationEntity applicationEntity2;
    private UUID applicationId1;
    private UUID applicationId2;

    @BeforeEach
    void setUp() {
        applicationId1 = UUID.randomUUID();
        applicationId2 = UUID.randomUUID();

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

        applicationEntity1 = new ApplicationEntity(
            applicationId1, 123456789L, 10000.0, 36.0, 1, 1);
        applicationEntity2 = new ApplicationEntity(
            applicationId2, 987654321L, 20000.0, 60.0, 1, 1);


    }

    @Test
    @DisplayName("Should get all applications successfully")
    void shouldGetAllApplicationsSuccessfully() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(applicationEntity1, applicationEntity2));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);
        when(mapper.map(applicationEntity2, Application.class)).thenReturn(application2);

        // Act & Assert
        StepVerifier.create(adapter.getAllApplications())
            .expectNext(application1, application2)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getAllApplications fails")
    void shouldReturnDataRetrievalExceptionWhenGetAllApplicationsFails() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getAllApplications())
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should get applications by user ID number successfully")
    void shouldGetApplicationsByUserIdNumberSuccessfully() {
        // Arrange
        Long userIdNumber = 123456789L;
        when(repository.findByUserIdNumber(userIdNumber)).thenReturn(Flux.just(applicationEntity1));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByUserIdNumber(userIdNumber))
            .expectNext(application1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getApplicationsByUserIdNumber fails")
    void shouldReturnDataRetrievalExceptionWhenGetApplicationsByUserIdNumberFails() {
        // Arrange
        Long userIdNumber = 123456789L;
        when(repository.findByUserIdNumber(userIdNumber)).thenReturn(Flux.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByUserIdNumber(userIdNumber))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should get application by application ID successfully")
    void shouldGetApplicationByApplicationIdSuccessfully() {
        // Arrange
        UUID applicationId = applicationId1;
        when(repository.findById(applicationId)).thenReturn(Mono.just(applicationEntity1));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByApplicationId(applicationId))
            .expectNext(application1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getApplicationsByApplicationId fails")
    void shouldReturnDataRetrievalExceptionWhenGetApplicationsByApplicationIdFails() {
        // Arrange
        UUID applicationId = applicationId1;
        when(repository.findById(applicationId)).thenReturn(Mono.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByApplicationId(applicationId))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should save application successfully")
    void shouldSaveApplicationSuccessfully() {
        // Arrange
        when(repository.save(any(ApplicationEntity.class))).thenReturn(Mono.just(applicationEntity1));
        when(mapper.map(application1, ApplicationEntity.class)).thenReturn(applicationEntity1);
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Act & Assert
        StepVerifier.create(adapter.saveApplication(Mono.just(application1)))
            .expectNext(application1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataPersistenceException when saveApplication fails")
    void shouldReturnDataPersistenceExceptionWhenSaveApplicationFails() {
        // Arrange
        when(repository.save(any(ApplicationEntity.class))).thenReturn(Mono.error(new RuntimeException("Simulated save error")));
        when(mapper.map(application1, ApplicationEntity.class)).thenReturn(applicationEntity1);  //Needed so the mapping doesn't fail before the save.

        // Act & Assert
        StepVerifier.create(adapter.saveApplication(Mono.just(application1)))
            .expectError(DataPersistenceException.class)
            .verify();
    }
}
