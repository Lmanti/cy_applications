package co.com.crediya.model.loanstatus.gateways;

import co.com.crediya.model.loanstatus.LoanStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanStatusRepository {
    Flux<LoanStatus> getAllLoanStatuses();
    Mono<LoanStatus> getLoanStatusById(int loanStatusId);
}
