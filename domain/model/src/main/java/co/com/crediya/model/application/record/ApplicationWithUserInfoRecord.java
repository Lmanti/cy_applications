package co.com.crediya.model.application.record;

import java.util.UUID;

import co.com.crediya.model.loanstatus.LoanStatus;
import co.com.crediya.model.loantype.LoanType;

public record ApplicationWithUserInfoRecord (
    UUID applicationId,
    Long userIdNumber,
    String email,
    String name,
    String lastname,
    Double baseSalary,
    Double loanAmount,
    Double loanTerm,
    LoanType loanType,
    LoanStatus loanStatus,
    Double monthlyAmmout
) {}
