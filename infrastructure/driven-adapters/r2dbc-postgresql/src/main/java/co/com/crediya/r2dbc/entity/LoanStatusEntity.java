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
@Table("loan_status")
public class LoanStatusEntity {
    @Id
    @Column("loan_status_id")
    private Integer loanStatusId;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
}
