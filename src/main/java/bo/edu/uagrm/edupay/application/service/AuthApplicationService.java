package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.in.security.JwtProvider;
import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.EmployeeJpaRepository;
import bo.edu.uagrm.edupay.application.dto.EmployeeView;
import bo.edu.uagrm.edupay.application.dto.LoginRequest;
import bo.edu.uagrm.edupay.application.dto.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthApplicationService {

    private final EmployeeJpaRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthApplicationService(EmployeeJpaRepository employeeRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<EmployeeEntity> optEmployee = employeeRepository.findByEmail(request.email());
        
        if (optEmployee.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        
        EmployeeEntity employee = optEmployee.get();
        
        if (!passwordEncoder.matches(request.password(), employee.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        if (!employee.isActive()) {
            throw new RuntimeException("Account is inactive");
        }

        String token = jwtProvider.createToken(employee.getErpCode(), employee.getRole());
        EmployeeView view = new EmployeeView(employee.getErpCode(), employee.getName(), employee.getEmail(), employee.getRole());
        
        return new LoginResponse(token, view);
    }

    public EmployeeEntity getEmployeeFromToken(String token) {
        String erpCode = jwtProvider.validateAndGetSubject(token);
        EmployeeEntity employee = employeeRepository.findByErpCode(erpCode)
                .orElseThrow(() -> new RuntimeException("Employee not found for code: " + erpCode));
        if (!employee.isActive()) {
            throw new RuntimeException("Employee account is inactive");
        }
        return employee;
    }
}
