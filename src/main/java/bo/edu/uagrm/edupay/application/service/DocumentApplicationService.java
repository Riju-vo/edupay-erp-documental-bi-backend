package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.*;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.*;
import bo.edu.uagrm.edupay.adapters.out.storage.S3StorageService;
import bo.edu.uagrm.edupay.application.dto.DocumentView;
import bo.edu.uagrm.edupay.application.dto.IssueInvoiceCommand;
import bo.edu.uagrm.edupay.application.dto.ReviewDocumentCommand;
import bo.edu.uagrm.edupay.application.port.in.DocumentUseCase;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentApplicationService implements DocumentUseCase {
    private final DocumentJpaRepository documentRepository;
    private final DocumentReviewJpaRepository reviewRepository;
    private final ReceiptInvoiceJpaRepository invoiceRepository;
    private final OutboxEventJpaRepository outboxRepository;
    private final FamilyJpaRepository familyRepository;
    private final S3StorageService s3;

    public DocumentApplicationService(DocumentJpaRepository documentRepository,
            DocumentReviewJpaRepository reviewRepository,
            ReceiptInvoiceJpaRepository invoiceRepository,
            OutboxEventJpaRepository outboxRepository,
            FamilyJpaRepository familyRepository,
            S3StorageService s3) {
        this.documentRepository = documentRepository;
        this.reviewRepository = reviewRepository;
        this.invoiceRepository = invoiceRepository;
        this.outboxRepository = outboxRepository;
        this.familyRepository = familyRepository;
        this.s3 = s3;
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
        String storageKey = S3StorageService.invoiceKey(command.familyId(), command.periodCode());
        // En producción: s3.upload(storageKey, pdfBytes, "application/pdf")
        // En desarrollo local sin AWS: la key se guarda igual en BD

        ReceiptInvoiceEntity invoice = new ReceiptInvoiceEntity();
        invoice.setFamilyId(command.familyId());
        invoice.setPeriodCode(command.periodCode());
        invoice.setAmount(BigDecimal.valueOf(command.amount()));
        invoice.setStorageKey(storageKey);
        invoiceRepository.save(invoice);

        OutboxEventEntity event = new OutboxEventEntity();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("InvoiceGenerated");
        event.setAggregateType("DOC_RECEIPT_INVOICE");
        event.setAggregateId(invoice.getId().toString());
        event.setPayload("{\"invoiceId\":" + invoice.getId() + ",\"storageKey\":\"" + storageKey + "\"}");
        outboxRepository.save(event);
        return invoice.getId();
    }

    @Override
    @Transactional
    public DocumentView registerDocument(Long familyId, String documentType, String storageKey, String uploadedBy) {
        DocumentEntity doc = new DocumentEntity();
        doc.setFamilyId(familyId);
        doc.setDocumentType(documentType);
        doc.setStorageKey(storageKey);
        doc.setStatus("PENDING");
        doc.setUploadedBy(uploadedBy);
        documentRepository.save(doc);

        String familyName = familyRepository.findById(familyId)
                .map(FamilyEntity::getTutorName)
                .orElse("Desconocido");

        return new DocumentView(doc.getId(), familyId, familyName, documentType,
                storageKey, "PENDING", uploadedBy, doc.getUploadedAt().toString());
    }

    @Override
    public List<DocumentView> listDocuments(String status) {
        List<DocumentEntity> docs = (status != null && !status.isBlank())
                ? documentRepository.findByStatus(status)
                : documentRepository.findAll();

        return docs.stream().map(doc -> {
            String familyName = familyRepository.findById(doc.getFamilyId())
                    .map(FamilyEntity::getTutorName)
                    .orElse("Desconocido");
            return new DocumentView(
                    doc.getId(),
                    doc.getFamilyId(),
                    familyName,
                    doc.getDocumentType(),
                    doc.getStorageKey(),
                    doc.getStatus(),
                    doc.getUploadedBy(),
                    doc.getUploadedAt().toString());
        }).toList();
    }
}
