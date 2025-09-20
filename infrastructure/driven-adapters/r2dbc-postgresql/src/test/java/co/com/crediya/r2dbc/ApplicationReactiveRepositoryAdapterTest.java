package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.SearchCriteria;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationReactiveRepositoryAdapterTest {

    @Mock
    private ApplicationReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private DatabaseClient.GenericExecuteSpec genericExecuteSpec;

    @Mock
    private RowsFetchSpec<ApplicationEntity> rowsFetchSpec;

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

        applicationEntity1 = new ApplicationEntity();
        applicationEntity1.setApplicationId(applicationId1);
        applicationEntity1.setUserEmail("test1@example.com");
        applicationEntity1.setLoanAmount(10000.0);
        applicationEntity1.setLoanTerm(36.0);
        applicationEntity1.setLoanTypeId(1);
        applicationEntity1.setLoanStatusId(1);

        applicationEntity2 = new ApplicationEntity();
        applicationEntity2.setApplicationId(applicationId2);
        applicationEntity2.setUserEmail("test2@example.com");
        applicationEntity2.setLoanAmount(20000.0);
        applicationEntity2.setLoanTerm(60.0);
        applicationEntity2.setLoanTypeId(1);
        applicationEntity2.setLoanStatusId(1);
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
    @DisplayName("Should get applications by user email successfully")
    void shouldGetApplicationsByUserEmailSuccessfully() {
        // Arrange
        String userEmail = "test1@example.com";
        when(repository.findByUserEmail(userEmail)).thenReturn(Flux.just(applicationEntity1));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByUserEmail(userEmail))
            .expectNext(application1)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return DataRetrievalException when getApplicationsByUserEmail fails")
    void shouldReturnDataRetrievalExceptionWhenGetApplicationsByUserEmailFails() {
        // Arrange
        String userEmail = "test1@example.com";
        when(repository.findByUserEmail(userEmail)).thenReturn(Flux.error(new RuntimeException("Simulated error")));

        // Act & Assert
        StepVerifier.create(adapter.getApplicationsByUserEmail(userEmail))
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
        when(mapper.map(application1, ApplicationEntity.class)).thenReturn(applicationEntity1);
        when(repository.save(any(ApplicationEntity.class))).thenReturn(Mono.just(applicationEntity1));
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
        when(mapper.map(application1, ApplicationEntity.class)).thenReturn(applicationEntity1);
        when(repository.save(any(ApplicationEntity.class))).thenReturn(Mono.error(new RuntimeException("Simulated save error")));

        // Act & Assert
        StepVerifier.create(adapter.saveApplication(Mono.just(application1)))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should find applications by criteria successfully")
    void shouldFindByCriteriaSuccessfully() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("loan_amount")
                .sortDirection("asc")
                .filters(Map.of("loan_type_id", 1))
                .build();

        List<ApplicationEntity> applicationEntities = Collections.singletonList(applicationEntity1);

        // Crear el mock para la consulta de conteo primero
        DatabaseClient.GenericExecuteSpec countSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<Long> countRowsFetchSpec = mock(RowsFetchSpec.class);

        // Configurar el comportamiento de databaseClient.sql() para devolver diferentes mocks
        when(databaseClient.sql(anyString()))
            .thenReturn(genericExecuteSpec)  // Para la primera llamada (consulta de datos)
            .thenReturn(countSpec);          // Para la segunda llamada (consulta de conteo)

        // Configuración para la consulta principal
        doReturn(genericExecuteSpec).when(genericExecuteSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(rowsFetchSpec).when(genericExecuteSpec).map(any(BiFunction.class));
        when(rowsFetchSpec.all()).thenReturn(Flux.fromIterable(applicationEntities));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Configuración para la consulta de conteo
        doReturn(countSpec).when(countSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(countRowsFetchSpec).when(countSpec).map(any(BiFunction.class));
        when(countRowsFetchSpec.first()).thenReturn(Mono.just(1L));

        // Act & Assert
        StepVerifier.create(adapter.findByCriteria(criteria))
                .expectNextMatches(pageResult ->
                        pageResult.getContent().size() == 1 &&
                                pageResult.getTotalElements() == 1 &&
                                pageResult.getContent().get(0).equals(application1))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should handle empty filters and sorting successfully")
    void shouldHandleEmptyFiltersAndSortingSuccessfully() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .build();

        List<ApplicationEntity> applicationEntities = Collections.singletonList(applicationEntity1);

        // Crear el mock para la consulta de conteo primero
        DatabaseClient.GenericExecuteSpec countSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<Long> countRowsFetchSpec = mock(RowsFetchSpec.class);

        // Configurar el comportamiento de databaseClient.sql() para devolver diferentes mocks
        when(databaseClient.sql(anyString()))
            .thenReturn(genericExecuteSpec)  // Para la primera llamada (consulta de datos)
            .thenReturn(countSpec);          // Para la segunda llamada (consulta de conteo)

        // Configuración para la consulta principal
        doReturn(genericExecuteSpec).when(genericExecuteSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(rowsFetchSpec).when(genericExecuteSpec).map(any(BiFunction.class));
        when(rowsFetchSpec.all()).thenReturn(Flux.fromIterable(applicationEntities));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Configuración para la consulta de conteo
        doReturn(countSpec).when(countSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(countRowsFetchSpec).when(countSpec).map(any(BiFunction.class));
        when(countRowsFetchSpec.first()).thenReturn(Mono.just(1L));

        // Act & Assert
        StepVerifier.create(adapter.findByCriteria(criteria))
                .expectNextMatches(pageResult ->
                        pageResult.getContent().size() == 1 &&
                                pageResult.getTotalElements() == 1 &&
                                pageResult.getContent().get(0).equals(application1))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should throw DataRetrievalException when executeQuery fails")
    void shouldThrowDataRetrievalExceptionWhenExecuteQueryFails() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("loan_amount")
                .sortDirection("asc")
                .filters(Map.of("loan_type_id", 1))
                .build();

        // Crear el mock para la consulta de conteo
        DatabaseClient.GenericExecuteSpec countSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<Long> countRowsFetchSpec = mock(RowsFetchSpec.class);

        // Configurar el comportamiento de databaseClient.sql() para devolver diferentes mocks
        when(databaseClient.sql(anyString()))
            .thenReturn(genericExecuteSpec)  // Para la primera llamada (consulta de datos)
            .thenReturn(countSpec);          // Para la segunda llamada (consulta de conteo)

        // Configuración para la consulta principal (que fallará)
        doReturn(genericExecuteSpec).when(genericExecuteSpec).bind(anyInt(), any());
        doReturn(rowsFetchSpec).when(genericExecuteSpec).map(any(BiFunction.class));
        when(rowsFetchSpec.all()).thenReturn(Flux.error(new RuntimeException("Simulated database error")));

        // Configuración para la consulta de conteo (aunque no se llegará a ella debido al error anterior)
        doReturn(countSpec).when(countSpec).bind(anyInt(), any());
        doReturn(countRowsFetchSpec).when(countSpec).map(any(BiFunction.class));
        when(countRowsFetchSpec.first()).thenReturn(Mono.just(1L));

        // Act & Assert
        StepVerifier.create(adapter.findByCriteria(criteria))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should throw DataRetrievalException when executeCountQuery fails")
    void shouldThrowDataRetrievalExceptionWhenExecuteCountQueryFails() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("loan_amount")
                .sortDirection("asc")
                .filters(Map.of("loan_type_id", 1))
                .build();

        // Crear el mock para la consulta de conteo primero
        DatabaseClient.GenericExecuteSpec countSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<Long> countRowsFetchSpec = mock(RowsFetchSpec.class);

        // Configurar el comportamiento de databaseClient.sql() para devolver diferentes mocks
        when(databaseClient.sql(anyString()))
            .thenReturn(genericExecuteSpec)  // Para la primera llamada (consulta de datos)
            .thenReturn(countSpec);          // Para la segunda llamada (consulta de conteo)

        // Configuración para la consulta principal
        doReturn(genericExecuteSpec).when(genericExecuteSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(rowsFetchSpec).when(genericExecuteSpec).map(any(BiFunction.class));
        when(rowsFetchSpec.all()).thenReturn(Flux.just(applicationEntity1));
        when(mapper.map(applicationEntity1, Application.class)).thenReturn(application1);

        // Configuración para la consulta de conteo
        doReturn(countSpec).when(countSpec).bind(anyInt(), any());
        // Corregir el método map para usar BiFunction en lugar de Function
        doReturn(countRowsFetchSpec).when(countSpec).map(any(BiFunction.class));
        when(countRowsFetchSpec.first()).thenReturn(Mono.error(new RuntimeException("Simulated count error")));

        // Act & Assert
        StepVerifier.create(adapter.findByCriteria(criteria))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid filter column")
    void shouldThrowIllegalArgumentExceptionForInvalidFilterColumn() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("loan_amount")
                .sortDirection("asc")
                .filters(Map.of("invalid_column", 1))
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adapter.findByCriteria(criteria));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid sort column")
    void shouldThrowIllegalArgumentExceptionForInvalidSortColumn() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("invalid_column")
                .sortDirection("asc")
                .filters(Map.of("loan_type_id", 1))
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adapter.findByCriteria(criteria));
    }
}