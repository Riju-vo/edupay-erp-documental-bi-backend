package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "erp_account_status")
@Getter
@Setter
public class AccountStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "family_id")
    private FamilyEntity family;

    @Column(name = "period_code", nullable = false)
    private String periodCode;

    @Column(name = "expected_amount", nullable = false)
    private BigDecimal expectedAmount;

    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;

    @Column(name = "debt_amount", nullable = false)
    private BigDecimal debtAmount;

    @Column(nullable = false)
    private String status;
}
