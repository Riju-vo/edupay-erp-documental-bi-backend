package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "doc_document_review")
@Getter
@Setter
public class DocumentReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "reviewer_user", nullable = false)
    private String reviewerUser;

    @Column(nullable = false)
    private String status;

    private String reason;

    @Column(name = "reviewed_at", nullable = false)
    private Instant reviewedAt = Instant.now();
}
