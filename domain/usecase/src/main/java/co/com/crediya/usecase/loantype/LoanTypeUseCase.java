package co.com.crediya.usecase.loantype;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeRepository loanTypeRepository;

    Flux<LoanType> getAllLoanTypes() {
        return loanTypeRepository.getAllLoanTypes();
    }

    Mono<LoanType> getLoanTypeById(int loanTypeId) {
        return loanTypeRepository.getLoanTypeById(loanTypeId);
    }
}
