package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "doc_receipt_invoice")
@Getter
@Setter
public class ReceiptInvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "period_code", nullable = false)
    private String periodCode;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
