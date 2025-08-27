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
    private Integer loanAmount;
    private Integer loanTerm;
    private Integer loanTypeId;
    private Integer loanStatusId;
}
