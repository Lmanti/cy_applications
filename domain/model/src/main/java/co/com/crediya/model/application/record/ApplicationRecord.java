package co.com.crediya.model.application.record;

import java.util.UUID;

import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loantype.LoanType;

public record ApplicationRecord (
    UUID applicationId,
    Long userIdNumber,
    Double loanAmount,
    Double loanTerm,
    LoanType loanType,
    LoanStatus loanStatus
) {}
