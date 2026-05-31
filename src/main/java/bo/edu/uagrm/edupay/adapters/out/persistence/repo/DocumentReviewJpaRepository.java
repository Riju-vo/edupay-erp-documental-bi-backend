package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.DocumentReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentReviewJpaRepository extends JpaRepository<DocumentReviewEntity, Long> {
}
