package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.DiscountAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountAssignmentJpaRepository extends JpaRepository<DiscountAssignmentEntity, Long> {
}
