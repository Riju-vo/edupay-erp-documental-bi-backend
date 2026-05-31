package bo.edu.uagrm.edupay.adapters.in.events;

import bo.edu.uagrm.edupay.adapters.in.graphql.GraphqlController;
import bo.edu.uagrm.edupay.application.dto.PaymentConfirmedEvent;
import bo.edu.uagrm.edupay.application.port.in.IntegrationUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integration/events")
public class IntegrationEventController {
    private final IntegrationUseCase integrationUseCase;
    private final GraphqlController graphqlController;

    public IntegrationEventController(IntegrationUseCase integrationUseCase, GraphqlController graphqlController) {
        this.integrationUseCase = integrationUseCase;
        this.graphqlController = graphqlController;
    }

    @PostMapping("/payment-confirmed")
    public ResponseEntity<String> paymentConfirmed(@Valid @RequestBody PaymentConfirmedRequest request) {
        boolean processed = integrationUseCase.processPaymentConfirmed(new PaymentConfirmedEvent(
                request.eventId(), request.paymentExternalId(), request.familyId(), request.paymentMethod(), request.amount()));

        if (processed) {
            graphqlController.emitPaymentEvent(request.eventId(), request.paymentExternalId(), request.paymentMethod(), request.amount());
            return ResponseEntity.accepted().body("processed");
        }
        return ResponseEntity.ok("duplicate");
    }

    public record PaymentConfirmedRequest(
            @NotBlank String eventId,
            @NotBlank String paymentExternalId,
            @NotNull Long familyId,
            @NotBlank String paymentMethod,
            double amount
    ) {}

    @PostMapping("/payment-reversed")
    public ResponseEntity<String> paymentReversed(@Valid @RequestBody PaymentReversedRequest request) {
        boolean processed = integrationUseCase.processPaymentReversed(new bo.edu.uagrm.edupay.application.dto.PaymentReversedEvent(
                request.eventId(), request.paymentExternalId(), request.familyId(), request.reason()));

        if (processed) {
            // Optional: graphqlController.emitPaymentReversedEvent(...)
            return ResponseEntity.accepted().body("processed");
        }
        return ResponseEntity.ok("duplicate");
    }

    public record PaymentReversedRequest(
            @NotBlank String eventId,
            @NotBlank String paymentExternalId,
            @NotNull Long familyId,
            @NotBlank String reason
    ) {}
}
