package bo.edu.uagrm.edupay.application.dto;

public record IssueInvoiceCommand(Long familyId, String periodCode, double amount) {}
