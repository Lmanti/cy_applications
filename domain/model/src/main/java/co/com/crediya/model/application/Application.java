package co.com.crediya.model.application;
import lombok.Builder;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {
    private UUID applicationId;
    private Long userIdNumber;
    private String userEmail;
    private Double loanAmount;
    private Double loanTerm;
    private Integer loanTypeId;
    private Integer loanStatusId;

    public Application(
        UUID applicationId,
        String userEmail,
        Double loanAmount,
        Double loanTerm,
        Integer loanTypeId,
        Integer loanStatusId
    ) {
        this.applicationId = applicationId;
        this.userEmail = userEmail;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanTypeId = loanTypeId;
        this.loanStatusId = loanStatusId;
    }

    public Application(
        UUID applicationId,
        Long userIdNumber,
        Double loanAmount,
        Double loanTerm,
        Integer loanTypeId,
        Integer loanStatusId
    ) {
        this.applicationId = applicationId;
        this.userIdNumber = userIdNumber;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanTypeId = loanTypeId;
        this.loanStatusId = loanStatusId;
    }
}
