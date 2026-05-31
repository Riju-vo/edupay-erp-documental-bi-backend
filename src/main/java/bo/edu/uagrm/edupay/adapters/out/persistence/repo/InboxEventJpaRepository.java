package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.InboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxEventJpaRepository extends JpaRepository<InboxEventEntity, Long> {
    boolean existsByEventId(String eventId);
}
