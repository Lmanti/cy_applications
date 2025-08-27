package co.com.crediya.usecase.loanstatus;

import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loanstatus.gateways.LoanStatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanStatusUseCase {
    private final LoanStatusRepository loanStatusRepository;

    Flux<LoanStatus> getAllLoanStatuses() {
        return loanStatusRepository.getAllLoanStatuses();
    }

    Mono<LoanStatus> getLoanStatusById(int loanStatusId) {
        return loanStatusRepository.getLoanStatusById(loanStatusId);
    }
}
