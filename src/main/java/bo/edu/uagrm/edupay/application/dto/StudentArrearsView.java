package bo.edu.uagrm.edupay.application.dto;

public record StudentArrearsView(
        String studentExternalId,
        String fullName,
        double pendingAmount
) {}
