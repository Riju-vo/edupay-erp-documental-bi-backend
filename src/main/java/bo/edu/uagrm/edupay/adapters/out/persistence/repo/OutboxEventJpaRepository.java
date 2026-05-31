package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, Long> {
}
