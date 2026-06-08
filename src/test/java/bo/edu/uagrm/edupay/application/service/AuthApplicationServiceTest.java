package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.in.security.JwtProvider;
import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.EmployeeJpaRepository;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthApplicationServiceTest {

    private EmployeeJpaRepository employeeRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = new JwtProvider(); // Using real JwtProvider so token creation/parsing matches
        authApplicationService = new AuthApplicationService(employeeRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void testGetEmployeeFromToken_Success() {
        EmployeeEntity mockEmployee = new EmployeeEntity();
        mockEmployee.setErpCode("EMP-ADMIN01");
        mockEmployee.setName("Admin");
        mockEmployee.setRole("ADMIN");
        mockEmployee.setActive(true);

        when(employeeRepository.findByErpCode("EMP-ADMIN01")).thenReturn(Optional.of(mockEmployee));

        String token = jwtProvider.createToken("EMP-ADMIN01", "ADMIN");

        EmployeeEntity result = authApplicationService.getEmployeeFromToken(token);

        assertNotNull(result);
        assertEquals("EMP-ADMIN01", result.getErpCode());
        assertEquals("Admin", result.getName());
        assertTrue(result.isActive());
    }

    @Test
    void testGetEmployeeFromToken_InactiveEmployee() {
        EmployeeEntity mockEmployee = new EmployeeEntity();
        mockEmployee.setErpCode("EMP-INACTIVE");
        mockEmployee.setName("Inactive");
        mockEmployee.setRole("ADMIN");
        mockEmployee.setActive(false);

        when(employeeRepository.findByErpCode("EMP-INACTIVE")).thenReturn(Optional.of(mockEmployee));

        String token = jwtProvider.createToken("EMP-INACTIVE", "ADMIN");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authApplicationService.getEmployeeFromToken(token);
        });

        assertEquals("Employee account is inactive", exception.getMessage());
    }

    @Test
    void testGetEmployeeFromToken_EmployeeNotFound() {
        when(employeeRepository.findByErpCode("EMP-NOTFOUND")).thenReturn(Optional.empty());

        String token = jwtProvider.createToken("EMP-NOTFOUND", "ADMIN");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authApplicationService.getEmployeeFromToken(token);
        });

        assertEquals("Employee not found for code: EMP-NOTFOUND", exception.getMessage());
    }

    @Test
    void testGetEmployeeFromToken_InvalidToken() {
        assertThrows(JwtException.class, () -> {
            authApplicationService.getEmployeeFromToken("invalid-token-string");
        });
    }
}
