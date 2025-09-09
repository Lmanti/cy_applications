package co.com.crediya.usecase.application;

import java.util.Map;
import java.util.UUID;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.application.record.ApplicationRecord;
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
}
