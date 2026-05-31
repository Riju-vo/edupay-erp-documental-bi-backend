package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bi_delinquency_snapshot")
@Getter
@Setter
public class DelinquencySnapshotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "families_in_arrears", nullable = false)
    private Integer familiesInArrears;

    @Column(name = "total_debt", nullable = false)
    private BigDecimal totalDebt;
}
