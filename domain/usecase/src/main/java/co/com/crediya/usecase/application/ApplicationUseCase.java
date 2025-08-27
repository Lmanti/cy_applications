package co.com.crediya.usecase.application;

import java.util.UUID;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {
    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;

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

    public Flux<Application> getAllApplications() {
        return applicationRepository.getAllApplications();
    }

    Flux<Application> getApplicationsByUserIdNumber(Long userIdNumber) {
        return applicationRepository.getApplicationsByUserIdNumber(userIdNumber);
    }

    Mono<Application> getApplicationsByApplicationId(UUID applicationId) {
        return applicationRepository.getApplicationsByApplicationId(applicationId);
    }
    
    Mono<Application> saveApplication(Mono<Application> application) {
        return application
        .flatMap(toSave -> loanTypeRepository.getLoanTypeById(toSave.getLoanTypeId())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        toSave.setLoanStatusId(LoanStatuses.PENDIENTE.getValue());
                        return applicationRepository.saveApplication(Mono.just(toSave));
                    } else {
                        return Mono.error(new InvalidDataException("No existe un tipo de prestamo con id " + toSave.getLoanTypeId()));
                    }
                })
        );
    }
}
