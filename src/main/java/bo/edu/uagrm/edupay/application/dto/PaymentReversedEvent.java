package bo.edu.uagrm.edupay.application.dto;

public record PaymentReversedEvent(String eventId, String paymentExternalId, Long familyId, String reason) {}
