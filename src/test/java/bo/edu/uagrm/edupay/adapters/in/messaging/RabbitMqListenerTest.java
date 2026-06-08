package bo.edu.uagrm.edupay.adapters.in.messaging;

import bo.edu.uagrm.edupay.adapters.in.graphql.GraphqlController;
import bo.edu.uagrm.edupay.adapters.in.messaging.dto.TokenValidationRequestDto;
import bo.edu.uagrm.edupay.adapters.in.messaging.dto.TokenValidationResponseDto;
import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import bo.edu.uagrm.edupay.application.port.in.IntegrationUseCase;
import bo.edu.uagrm.edupay.application.service.AuthApplicationService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMqListenerTest {

    private AuthApplicationService authApplicationService;
    private RabbitMqListener listener;

    @BeforeEach
    void setUp() {
        IntegrationUseCase integrationUseCase = mock(IntegrationUseCase.class);
        GraphqlController graphqlController = mock(GraphqlController.class);
        authApplicationService = mock(AuthApplicationService.class);
        listener = new RabbitMqListener(integrationUseCase, graphqlController, authApplicationService);
    }

    @Test
    void testHandleValidateToken_Success() {
        EmployeeEntity mockEmployee = new EmployeeEntity();
        mockEmployee.setErpCode("EMP-123");
        mockEmployee.setName("Test Employee");
        mockEmployee.setRole("ADMIN");

        when(authApplicationService.getEmployeeFromToken("valid-token")).thenReturn(mockEmployee);

        TokenValidationRequestDto request = new TokenValidationRequestDto("valid-token");
        TokenValidationResponseDto response = listener.handleValidateToken(request);

        assertNotNull(response);
        assertEquals("Success", response.message());
        assertEquals("EMP-123", response.employee().getErpCode());
        assertEquals("Test Employee", response.employee().getName());
    }

    @Test
    void testHandleValidateToken_ExpiredToken() {
        when(authApplicationService.getEmployeeFromToken("expired-token"))
                .thenThrow(new JwtException("JWT expired"));

        TokenValidationRequestDto request = new TokenValidationRequestDto("expired-token");
        TokenValidationResponseDto response = listener.handleValidateToken(request);

        assertNotNull(response);
        assertNull(response.employee());
        assertEquals("JWT expired", response.message());
    }

    @Test
    void testHandleValidateToken_EmployeeNotFound() {
        when(authApplicationService.getEmployeeFromToken("nonexistent-token"))
                .thenThrow(new RuntimeException("Employee not found for code: EMP-999"));

        TokenValidationRequestDto request = new TokenValidationRequestDto("nonexistent-token");
        TokenValidationResponseDto response = listener.handleValidateToken(request);

        assertNotNull(response);
        assertNull(response.employee());
        assertEquals("Employee not found for code: EMP-999", response.message());
    }
}
