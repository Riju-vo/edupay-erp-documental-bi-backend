package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByEmail(String email);
    Optional<EmployeeEntity> findByErpCode(String erpCode);
}
