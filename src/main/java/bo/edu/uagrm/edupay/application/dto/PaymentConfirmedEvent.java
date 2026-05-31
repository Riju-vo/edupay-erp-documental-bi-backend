package bo.edu.uagrm.edupay.application.dto;

public record PaymentConfirmedEvent(String eventId, String paymentExternalId, long familyId, String paymentMethod, double amount) {}
