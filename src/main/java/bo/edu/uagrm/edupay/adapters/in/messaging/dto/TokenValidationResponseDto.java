package bo.edu.uagrm.edupay.adapters.in.messaging.dto;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.EmployeeEntity;
import java.io.Serializable;

public record TokenValidationResponseDto(
        EmployeeEntity employee,
        String message,
        Boolean isSuccess
) implements Serializable {}
