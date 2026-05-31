package bo.edu.uagrm.edupay.application.port.in;

import bo.edu.uagrm.edupay.application.dto.PaymentConfirmedEvent;

import bo.edu.uagrm.edupay.application.dto.PaymentReversedEvent;

public interface IntegrationUseCase {
    boolean processPaymentConfirmed(PaymentConfirmedEvent event);
    boolean processPaymentReversed(PaymentReversedEvent event);
}
