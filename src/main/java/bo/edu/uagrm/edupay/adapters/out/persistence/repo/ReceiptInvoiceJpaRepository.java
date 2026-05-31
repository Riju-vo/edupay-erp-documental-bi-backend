package bo.edu.uagrm.edupay.adapters.out.persistence.repo;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.ReceiptInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptInvoiceJpaRepository extends JpaRepository<ReceiptInvoiceEntity, Long> {
}
