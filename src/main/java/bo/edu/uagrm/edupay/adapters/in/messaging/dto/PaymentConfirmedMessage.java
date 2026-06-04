package bo.edu.uagrm.edupay.adapters.in.messaging.dto;

import java.io.Serializable;

public record PaymentConfirmedMessage(
    String eventId,
    String paymentExternalId,
    Long familyId,
    String paymentMethod,
    double amount
) implements Serializable {}
