package bo.edu.uagrm.edupay.adapters.in.messaging.dto;

import java.io.Serializable;

public record PaymentReversedMessage(
    String eventId,
    String paymentExternalId,
    Long familyId,
    String reason
) implements Serializable {}
