package bo.edu.uagrm.edupay.application.dto;

import bo.edu.uagrm.edupay.domain.model.DocumentReviewStatus;

public record ReviewDocumentCommand(Long documentId, String reviewerUser, DocumentReviewStatus status, String reason) {}
