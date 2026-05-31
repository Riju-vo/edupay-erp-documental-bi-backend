package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyJpaRepository extends JpaRepository<FamilyEntity, Long> {
    Optional<FamilyEntity> findByExternalId(String externalId);
}
