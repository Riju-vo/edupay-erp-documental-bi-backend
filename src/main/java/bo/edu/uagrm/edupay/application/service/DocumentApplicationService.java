package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.*;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.*;
import bo.edu.uagrm.edupay.application.dto.IssueInvoiceCommand;
import bo.edu.uagrm.edupay.application.dto.ReviewDocumentCommand;
import bo.edu.uagrm.edupay.application.port.in.DocumentUseCase;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DocumentApplicationService implements DocumentUseCase {
    private final DocumentJpaRepository documentRepository;
    private final DocumentReviewJpaRepository reviewRepository;
    private final ReceiptInvoiceJpaRepository invoiceRepository;
    private final OutboxEventJpaRepository outboxRepository;

    public DocumentApplicationService(DocumentJpaRepository documentRepository,
                                      DocumentReviewJpaRepository reviewRepository,
                                      ReceiptInvoiceJpaRepository invoiceRepository,
                                      OutboxEventJpaRepository outboxRepository) {
        this.documentRepository = documentRepository;
        this.reviewRepository = reviewRepository;
        this.invoiceRepository = invoiceRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public Long reviewDocument(ReviewDocumentCommand command) {
        DocumentEntity document = documentRepository.findById(command.documentId()).orElseThrow();
        document.setStatus(command.status().name());
        documentRepository.save(document);

        DocumentReviewEntity review = new DocumentReviewEntity();
        review.setDocumentId(document.getId());
        review.setReviewerUser(command.reviewerUser());
        review.setStatus(command.status().name());
        review.setReason(command.reason());
        reviewRepository.save(review);

        OutboxEventEntity event = new OutboxEventEntity();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("APPROVED".equals(command.status().name()) ? "DocumentApproved" : "DocumentRejected");
        event.setAggregateType("DOC_DOCUMENT");
        event.setAggregateId(document.getId().toString());
        event.setPayload("{\"documentId\":" + document.getId() + ",\"status\":\"" + command.status().name() + "\"}");
        outboxRepository.save(event);

        return review.getId();
    }

    @Override
    @Transactional
    public Long issueInvoice(IssueInvoiceCommand command) {
        ReceiptInvoiceEntity invoice = new ReceiptInvoiceEntity();
        invoice.setFamilyId(command.familyId());
        invoice.setPeriodCode(command.periodCode());
        invoice.setAmount(BigDecimal.valueOf(command.amount()));
        invoice.setStorageKey("invoices/" + command.familyId() + "/" + command.periodCode() + ".pdf");
        invoiceRepository.save(invoice);

        OutboxEventEntity event = new OutboxEventEntity();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("InvoiceGenerated");
        event.setAggregateType("DOC_RECEIPT_INVOICE");
        event.setAggregateId(invoice.getId().toString());
        event.setPayload("{\"invoiceId\":" + invoice.getId() + "}");
        outboxRepository.save(event);
        return invoice.getId();
    }
}
