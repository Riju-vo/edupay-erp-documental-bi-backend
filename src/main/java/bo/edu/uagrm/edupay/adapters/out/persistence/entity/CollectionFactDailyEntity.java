package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bi_collection_fact_daily")
@Getter
@Setter
public class CollectionFactDailyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "external_payment_id", nullable = false, unique = true)
    private String externalPaymentId;
}
