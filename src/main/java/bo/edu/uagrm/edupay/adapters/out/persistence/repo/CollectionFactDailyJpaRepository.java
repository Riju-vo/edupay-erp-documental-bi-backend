package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.CollectionFactDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CollectionFactDailyJpaRepository extends JpaRepository<CollectionFactDailyEntity, Long> {
    @Query("select c.paymentMethod, sum(c.amount) from CollectionFactDailyEntity c where c.businessDate between ?1 and ?2 group by c.paymentMethod")
    List<Object[]> sumByMethod(LocalDate from, LocalDate to);

    @Query("select coalesce(sum(c.amount), 0) from CollectionFactDailyEntity c where c.businessDate between ?1 and ?2")
    java.math.BigDecimal totalBetween(LocalDate from, LocalDate to);
}
