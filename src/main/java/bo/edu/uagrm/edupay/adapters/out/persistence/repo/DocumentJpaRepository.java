package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {
}
