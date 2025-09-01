package co.com.crediya.r2dbc.entity;

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
@Table("loan_type")
public class LoanTypeEntity {
    @Id
    @Column("loan_type_id")
    private Integer loanTypeId;
    @Column("name")
    private String name;
    @Column("min_amount")
    private Double minAmount;
    @Column("max_amoun")
    private Double maxAmount;
    @Column("interest_rate")
    private Double interestRate;
    @Column("auto_validation")
    private Boolean autoValidation;
}
