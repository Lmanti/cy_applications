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
        PENDIENTE(2);

        private final int value;

        private LoanStatuses(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Flux<ApplicationRecord> getAllApplications() {
        Mono<Map<Integer, LoanType>> loanTypeMapMono = loanTypeRepository.getAllLoanTypes()
            .collectMap(LoanType::getLoanTypeId);
        Mono<Map<Integer, LoanStatus>> loanStatusMapMono = loanStatusRepository.getAllLoanStatuses()
            .collectMap(LoanStatus::getLoanStatusId);

        return Flux.zip(loanTypeMapMono, loanStatusMapMono)
            .flatMap(tuple -> {
                Map<Integer, LoanType> loanTypeMap = tuple.getT1();
                Map<Integer, LoanStatus> loanStatusMap = tuple.getT2();

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
        Mono<Map<Integer, LoanType>> loanTypeMapMono = loanTypeRepository.getAllLoanTypes()
            .collectMap(LoanType::getLoanTypeId);
        Mono<Map<Integer, LoanStatus>> loanStatusMapMono = loanStatusRepository.getAllLoanStatuses()
            .collectMap(LoanStatus::getLoanStatusId);

        return Flux.zip(loanTypeMapMono, loanStatusMapMono)
            .flatMap(tuple -> {
                Map<Integer, LoanType> loanTypeMap = tuple.getT1();
                Map<Integer, LoanStatus> loanStatusMap = tuple.getT2();

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
        Mono<Map<Integer, LoanType>> loanTypeMapMono = loanTypeRepository.getAllLoanTypes()
            .collectMap(LoanType::getLoanTypeId);
        Mono<Map<Integer, LoanStatus>> loanStatusMapMono = loanStatusRepository.getAllLoanStatuses()
            .collectMap(LoanStatus::getLoanStatusId);

        return Mono.zip(loanTypeMapMono, loanStatusMapMono)
            .flatMap(tuple -> {
                Map<Integer, LoanType> loanTypeMap = tuple.getT1();
                Map<Integer, LoanStatus> loanStatusMap = tuple.getT2();

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
    
    public Mono<Application> saveApplication(Mono<Application> application) {
        return application
            .flatMap(toSave ->
                loanTypeRepository.getLoanTypeById(toSave.getLoanTypeId()).hasElement()
                    .zipWith(userGateway.existByIdNumber(toSave.getUserIdNumber()))
                    .flatMap(exists -> {
                        Boolean loanTypeExist = exists.getT1();
                        Boolean userExist = exists.getT2();

                        if (!loanTypeExist) {
                            return Mono.error(new InvalidDataException("No existe un tipo de préstamo con id " + toSave.getLoanTypeId()));
                        }
                        
                        if (!userExist) {
                            return Mono.error(new InvalidDataException("No existe un usuario con número de identificación " + toSave.getUserIdNumber()));
                        }

                        toSave.setLoanStatusId(LoanStatuses.PENDIENTE.getValue());
                        return applicationRepository.saveApplication(Mono.just(toSave));
                    })
        );
    }
}
