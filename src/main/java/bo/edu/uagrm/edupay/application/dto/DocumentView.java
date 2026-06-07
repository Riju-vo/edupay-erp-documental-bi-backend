package bo.edu.uagrm.edupay.application.dto;

public record DocumentView(
        Long id,
        Long familyId,
        String familyName,
        String documentType,
        String storageKey,
        String status,
        String uploadedBy,
        String uploadedAt) {
}
