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
    @Column("user_email")
    private String userEmail;
    @Column("loan_amount")
    private Double loanAmount;
    @Column("loan_term")
    private Double loanTerm;
    @Column("loan_type_id")
    private Integer loanTypeId;
    @Column("loan_status_id")
    private Integer loanStatusId;
}
