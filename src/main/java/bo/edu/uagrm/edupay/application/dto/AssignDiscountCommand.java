package bo.edu.uagrm.edupay.application.dto;

public record AssignDiscountCommand(Long familyId, String discountCode, double percentage) {}
