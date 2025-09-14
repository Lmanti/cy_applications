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
import co.com.crediya.model.application.gateways.ApplicationRepository;
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
                        application.getUserIdNumber(),
                        application.getLoanAmount(),
                        application.getLoanTerm(),
                        loanTypeMap.get(application.getLoanTypeId()),
                        loanStatusMap.get(application.getLoanStatusId())));
            });
    }

    public Flux<ApplicationRecord> getApplicationsByUserIdNumber(Long userIdNumber) {
        return Flux.zip(
                loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId)
            )
            .flatMap(params -> {
                Map<Integer, LoanType> loanTypeMap = params.getT1();
                Map<Integer, LoanStatus> loanStatusMap = params.getT2();

                return applicationRepository.getApplicationsByUserIdNumber(userIdNumber)
                    .map(application -> new ApplicationRecord(
                        application.getApplicationId(),
                        application.getUserIdNumber(),
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
                        application.getUserIdNumber(),
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
                userGateway.existByIdNumber(toSave.getUserIdNumber()),
                loanStatusRepository.getLoanStatusById(LoanStatuses.PENDIENTE.getValue())
            )
            .flatMap(params -> {
                Boolean userExist = params.getT2();

                if (!userExist) return Mono.error(new InvalidDataException("No existe un usuario con número de identificación " + toSave.getUserIdNumber()));

                toSave.setLoanStatusId(params.getT3().getLoanStatusId());
                return applicationRepository.saveApplication(Mono.just(toSave))
                    .map(savedApplication -> new ApplicationRecord(
                        savedApplication.getApplicationId(),
                        savedApplication.getUserIdNumber(),
                        savedApplication.getLoanAmount(),
                        savedApplication.getLoanTerm(),
                        params.getT1(),
                        params.getT3()));
            })
        );
    }

    public Mono<PageResult<ApplicationWithUserInfoRecord>> getByCriteriaPaginated(SearchCriteria criteria) {
        return Mono.zip(
                    loanTypeRepository.getAllLoanTypes().collectMap(LoanType::getLoanTypeId),
                    loanStatusRepository.getAllLoanStatuses().collectMap(LoanStatus::getLoanStatusId),
                    userGateway.getAllUsersBasicInfo().collectMap(UserBasicInfo::idNumber)
                )
                .flatMap(params -> applicationRepository.findByCriteria(criteria)
                    .map(paginated -> {
                        Map<Integer, LoanType> loanTypeMap = params.getT1();
                        Map<Integer, LoanStatus> loanStatusMap = params.getT2();
                        Map<Long, UserBasicInfo> userBasicInfoMap = params.getT3();

                        List<ApplicationWithUserInfoRecord> transformedContent = paginated.getContent().stream()
                            .map(application -> new ApplicationWithUserInfoRecord(
                                application.getApplicationId(),
                                application.getUserIdNumber(),
                                userBasicInfoMap.get(application.getUserIdNumber()).email(),
                                userBasicInfoMap.get(application.getUserIdNumber()).name(),
                                userBasicInfoMap.get(application.getUserIdNumber()).lastname(),
                                userBasicInfoMap.get(application.getUserIdNumber()).baseSalary(),
                                application.getLoanAmount(),
                                application.getLoanTerm(),
                                loanTypeMap.get(application.getLoanTypeId()),
                                loanStatusMap.get(application.getLoanStatusId()),
                                calculateMonthlyPayment(application, loanTypeMap.get(application.getLoanTypeId()))
                            )).toList();
                        
                        return new PageResult<ApplicationWithUserInfoRecord>(
                            transformedContent,
                            paginated.getTotalElements(),
                            paginated.getCurrentPage(),
                            paginated.getSize()
                        );
                    }));
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
}
