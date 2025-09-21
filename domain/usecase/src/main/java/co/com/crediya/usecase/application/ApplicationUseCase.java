package co.com.crediya.usecase.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.PageResult;
import co.com.crediya.model.application.criteria.SearchCriteria;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.exception.UnauthorizedException;
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
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {
    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final UserGateway userGateway;
    private final NotificationsSQSGateway notificationsSQSGateway;

    private enum LoanStatuses {
        PENDIENTE(1);

        private final int value;

        private LoanStatuses(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private enum Roles {
        CLIENTE, ASESOR, ADMIN;
    }

    public Flux<ApplicationRecord> getAllApplications() {
        return Flux.zip(
                loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId)
            )
            .flatMap(params -> {
                Map<Integer, LoanType> loanTypeMap = params.getT1();
                Map<Integer, LoanStatus> loanStatusMap = params.getT2();

                return applicationRepository.getAllApplications()
                    .map(application -> new ApplicationRecord(
                        application.getApplicationId(),
                        application.getUserEmail(),
                        application.getLoanAmount(),
                        application.getLoanTerm(),
                        loanTypeMap.get(application.getLoanTypeId()),
                        loanStatusMap.get(application.getLoanStatusId())));
            });
    }

    public Flux<ApplicationRecord> getApplicationsByUserEmail(String userEmail) {
        return Flux.zip(
                loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId)
            )
            .flatMap(params -> {
                Map<Integer, LoanType> loanTypeMap = params.getT1();
                Map<Integer, LoanStatus> loanStatusMap = params.getT2();

                return applicationRepository.getApplicationsByUserEmail(userEmail)
                    .map(application -> new ApplicationRecord(
                        application.getApplicationId(),
                        application.getUserEmail(),
                        application.getLoanAmount(),
                        application.getLoanTerm(),
                        loanTypeMap.get(application.getLoanTypeId()),
                        loanStatusMap.get(application.getLoanStatusId())));
            });
    }

    public Mono<ApplicationRecord> getApplicationsByApplicationId(UUID applicationId) {
        return Mono.zip(
                loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId)
            )
            .flatMap(params -> {
                Map<Integer, LoanType> loanTypeMap = params.getT1();
                Map<Integer, LoanStatus> loanStatusMap = params.getT2();

                return applicationRepository.getApplicationsByApplicationId(applicationId)
                    .map(application -> new ApplicationRecord(
                        application.getApplicationId(),
                        application.getUserEmail(),
                        application.getLoanAmount(),
                        application.getLoanTerm(),
                        loanTypeMap.get(application.getLoanTypeId()),
                        loanStatusMap.get(application.getLoanStatusId())));
            });
    }

    public Mono<ApplicationRecord> saveApplication(Mono<Application> application) {
        return application.flatMap(toSave ->
            Mono.zip(
                loanTypeRepository.getLoanTypeById(toSave.getLoanTypeId()),
                userGateway.getUserByIdNumber(toSave.getUserIdNumber())
                    .switchIfEmpty(Mono.error(new InvalidDataException("No existe un usuario con email " + toSave.getUserEmail()))),
                loanStatusRepository.getLoanStatusById(LoanStatuses.PENDIENTE.getValue()),
                userGateway.getRequestUserByToken()
            )
            .flatMap(params -> {
                LoanStatus pendingStatus = params.getT3();
                UserBasicInfo authenticatedUser = params.getT4();

                if (!authenticatedUser.roleName().equals(Roles.CLIENTE.name())) return Mono.error(new UnauthorizedException("Para poder crear una solicitud necesita ser Cliente."));
                if (!authenticatedUser.idNumber().equals(toSave.getUserIdNumber())) return Mono.error(new InvalidDataException("No se permite crear solicitudes para otro usuario diferente al autenticado."));

                toSave.setUserEmail(authenticatedUser.email());
                toSave.setLoanStatusId(pendingStatus.getLoanStatusId());
                return applicationRepository.saveApplication(Mono.just(toSave))
                    .map(savedApplication -> new ApplicationRecord(
                        savedApplication.getApplicationId(),
                        savedApplication.getUserEmail(),
                        savedApplication.getLoanAmount(),
                        savedApplication.getLoanTerm(),
                        params.getT1(),
                        params.getT3()));
            })
        );
    }

    public Mono<PageResult<ApplicationWithUserInfoRecord>> getByCriteriaPaginated(SearchCriteria criteria) {
        return applicationRepository.findByCriteria(criteria)
            .flatMap(paginated -> {
                List<String> usersEmails = paginated.getContent().stream().map(Application::getUserEmail).toList();

                return Mono.zip(
                    loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                    loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId),
                    userGateway.getUsersBasicInfo(usersEmails).collectMap(UserBasicInfo::email)
                )
                .flatMap(params -> {
                    Map<Integer, LoanType> loanTypeMap = params.getT1();
                    Map<Integer, LoanStatus> loanStatusMap = params.getT2();
                    Map<String, UserBasicInfo> userBasicInfoMap = params.getT3();

                    List<ApplicationWithUserInfoRecord> transformedContent = paginated.getContent().stream()
                        .map(application -> new ApplicationWithUserInfoRecord(
                            application.getApplicationId(),
                            userBasicInfoMap.get(application.getUserEmail()).idNumber(),
                            userBasicInfoMap.get(application.getUserEmail()).email(),
                            userBasicInfoMap.get(application.getUserEmail()).name(),
                            userBasicInfoMap.get(application.getUserEmail()).lastname(),
                            userBasicInfoMap.get(application.getUserEmail()).baseSalary(),
                            application.getLoanAmount(),
                            application.getLoanTerm(),
                            loanTypeMap.get(application.getLoanTypeId()),
                            loanStatusMap.get(application.getLoanStatusId()),
                            calculateMonthlyPayment(application, loanTypeMap.get(application.getLoanTypeId()))
                        )).toList();

                        return Mono.just(new PageResult<ApplicationWithUserInfoRecord>(
                            transformedContent,
                            paginated.getTotalElements(),
                            paginated.getCurrentPage(),
                            paginated.getSize()
                        ));
                });
            });
    }

    private Double calculateMonthlyPayment(Application application, LoanType loanType) {
        Double anualInterestRate = loanType.getInterestRate();
        Double monthlyInterestRate = Math.pow(1 + anualInterestRate / 100, 1.0/12.0) - 1;
        Double monthlyAmount = application.getLoanAmount() * monthlyInterestRate / 
            (1 - Math.pow(1 + monthlyInterestRate, -application.getLoanTerm()));
        BigDecimal roundedAmount = BigDecimal.valueOf(monthlyAmount)
            .setScale(2, RoundingMode.HALF_UP);

        return roundedAmount.doubleValue();
    }

    public Mono<ApplicationWithUserInfoRecord> updateApplicationStatus(Mono<Application> application) {
        return application.flatMap(toEdit ->
            applicationRepository.getApplicationsByApplicationId(toEdit.getApplicationId())
                .switchIfEmpty(Mono.error(new InvalidDataException("No existe una solicitud de crédito con id: " + toEdit.getApplicationId())))
            .flatMap(existing ->
                Mono.zip(
                    loanTypeRepository.getLoanTypeById(existing.getLoanTypeId())
                        .switchIfEmpty(Mono.error(new InvalidDataException("No existe un tipo de crédito con id: " + existing.getLoanTypeId()))),
                    loanStatusRepository.getLoanStatusById(toEdit.getLoanStatusId())
                        .switchIfEmpty(Mono.error(new InvalidDataException("No existe un estado de crédito con id: " + toEdit.getLoanStatusId()))),
                    userGateway.getRequestUserByToken(),
                    userGateway.getUserByEmail(existing.getUserEmail())
                        .switchIfEmpty(Mono.error(new InvalidDataException("No existe un usuario con email " + existing.getUserEmail())))
                ).flatMap(params -> {
                    LoanType loanType = params.getT1();
                    LoanStatus loanStatus = params.getT2();
                    UserBasicInfo authenticatedUser = params.getT3();
                    UserBasicInfo applicationUser = params.getT4();

                    if (!authenticatedUser.roleName().equals(Roles.ASESOR.name())) return Mono.error(new UnauthorizedException("Para poder cambiar el estado de una solicitud necesita ser Asesor."));

                    existing.setLoanStatusId(loanStatus.getLoanStatusId());

                    return applicationRepository.updateApplication(Mono.just(existing))
                        .map(savedApplication -> new ApplicationWithUserInfoRecord(
                            savedApplication.getApplicationId(),
                            applicationUser.idNumber(),
                            savedApplication.getUserEmail(),
                            applicationUser.name(),
                            applicationUser.lastname(),
                            applicationUser.baseSalary(),
                            savedApplication.getLoanAmount(),
                            savedApplication.getLoanTerm(),
                            loanType,
                            loanStatus,
                            calculateMonthlyPayment(existing, loanType))
                        )
                        .flatMap(applicationRecord -> notificationsSQSGateway.send(applicationRecord)
                            .thenReturn(applicationRecord));
                })
            )
        );
    }
}
