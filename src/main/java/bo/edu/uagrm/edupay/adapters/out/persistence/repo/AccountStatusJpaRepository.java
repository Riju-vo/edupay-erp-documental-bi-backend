package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.AccountStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountStatusJpaRepository extends JpaRepository<AccountStatusEntity, Long> {
    List<AccountStatusEntity> findByFamily_Id(Long familyId);
}
