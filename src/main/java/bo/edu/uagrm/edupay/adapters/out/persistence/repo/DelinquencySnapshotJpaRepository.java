package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.DelinquencySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DelinquencySnapshotJpaRepository extends JpaRepository<DelinquencySnapshotEntity, Long> {
    Optional<DelinquencySnapshotEntity> findTopBySnapshotDateBetweenOrderBySnapshotDateDesc(LocalDate from, LocalDate to);
}
