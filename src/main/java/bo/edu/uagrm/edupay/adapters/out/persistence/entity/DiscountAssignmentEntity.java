package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "erp_discount_assignment")
@Getter
@Setter
public class DiscountAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "family_id")
    private FamilyEntity family;

    @Column(name = "discount_code", nullable = false)
    private String discountCode;

    @Column(nullable = false)
    private BigDecimal percentage;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt = Instant.now();
}
