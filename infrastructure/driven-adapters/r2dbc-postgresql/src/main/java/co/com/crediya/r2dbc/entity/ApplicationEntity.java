package co.com.crediya.r2dbc.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table("applications")
public class ApplicationEntity {
    @Id
    @Column("application_id")
    private UUID applicationId;
    @Column("user_id_number")
    private Long userIdNumber;
    @Column("loan_amount")
    private Integer loanAmount;
    @Column("loan_term")
    private Integer loanTerm;
    @Column("loan_type_id")
    private Integer loanTypeId;
    @Column("loan_status_id")
    private Integer loanStatusId;

    public ApplicationEntity(Long userIdNumber,
        Integer loanAmount,
        Integer loanTerm, 
        Integer loanTypeId,
        Integer loanStatusId
    ) {
        this.applicationId = UUID.randomUUID();
        this.userIdNumber = userIdNumber;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.loanTypeId = loanTypeId;
        this.loanStatusId = loanStatusId;
    }
}
