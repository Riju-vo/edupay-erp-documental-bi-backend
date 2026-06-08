package bo.edu.uagrm.edupay.adapters.in.messaging.dto;

import java.io.Serializable;

public record TokenValidationRequestDto(
        String token
) implements Serializable {}
