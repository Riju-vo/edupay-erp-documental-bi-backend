package bo.edu.uagrm.edupay.adapters.in.messaging;

import bo.edu.uagrm.edupay.adapters.in.graphql.GraphqlController;
import bo.edu.uagrm.edupay.adapters.in.messaging.dto.*;
import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import bo.edu.uagrm.edupay.application.dto.PaymentConfirmedEvent;
import bo.edu.uagrm.edupay.application.dto.PaymentReversedEvent;
import bo.edu.uagrm.edupay.application.port.in.IntegrationUseCase;
import bo.edu.uagrm.edupay.application.service.AuthApplicationService;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqListener.class);

    private final IntegrationUseCase integrationUseCase;
    private final GraphqlController graphqlController;
    private final AuthApplicationService authApplicationService;

    public RabbitMqListener(IntegrationUseCase integrationUseCase, GraphqlController graphqlController, AuthApplicationService authApplicationService) {
        this.integrationUseCase = integrationUseCase;
        this.graphqlController = graphqlController;
        this.authApplicationService = authApplicationService;
    }

    @RabbitListener(queues = RabbitMqConfig.ERP_CONFIRMED_QUEUE)
    public void handlePaymentConfirmed(PaymentConfirmedMessage message) {
        log.info("Mensaje de Pago Confirmado recibido de RabbitMQ: {}", message);

        boolean processed = integrationUseCase.processPaymentConfirmed(new PaymentConfirmedEvent(
                message.eventId(),
                message.paymentExternalId(),
                message.familyId(),
                message.paymentMethod(),
                message.amount()
        ));

        if (processed) {
            log.info("Pago confirmado procesado exitosamente (idempotente): {}", message.paymentExternalId());
            graphqlController.emitPaymentEvent(
                    message.eventId(),
                    message.paymentExternalId(),
                    message.paymentMethod(),
                    message.amount()
            );
        } else {
            log.warn("Pago confirmado duplicado o ya procesado (omitido): {}", message.paymentExternalId());
        }
    }

    @RabbitListener(queues = RabbitMqConfig.ERP_REVERSED_QUEUE)
    public void handlePaymentReversed(PaymentReversedMessage message) {
        log.info("Mensaje de Pago Reversado recibido de RabbitMQ: {}", message);

        boolean processed = integrationUseCase.processPaymentReversed(new PaymentReversedEvent(
                message.eventId(),
                message.paymentExternalId(),
                message.familyId(),
                message.reason()
        ));

        if (processed) {
            log.info("Reversión de pago procesada exitosamente (idempotente): {}", message.paymentExternalId());
        } else {
            log.warn("Reversión de pago duplicada o ya procesada (omitida): {}", message.paymentExternalId());
        }
    }

    @RabbitListener(queues = RabbitMqConfig.EMPLOYEE_VALIDATION_QUEUE)
    public TokenValidationResponseDto handleValidateToken(TokenValidationRequestDto request) {
        log.info("Mensaje de validacion de token recibido de RabbitMQ");
        try {
            EmployeeEntity employee = authApplicationService.getEmployeeFromToken(request.token());
            log.info("Token valido para empleado: {}", employee.getErpCode());
            return new TokenValidationResponseDto(employee, "Success");
        } catch (JwtException e) {
            log.warn("Error de validacion de token JWT: {}", e.getMessage());
            return new TokenValidationResponseDto(null, e.getMessage());
        } catch (Exception e) {
            log.warn("Error al buscar el empleado para el token: {}", e.getMessage());
            return new TokenValidationResponseDto(null, e.getMessage());
        }
    }

}
