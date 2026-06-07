package bo.edu.uagrm.edupay.application.dto;

public record FamilyFinancialStatusView(
        FamilyView family,
        double totalDebt,
        Double riskScore,
        int monthsPaid,
        int monthsPending,
        int monthsInArrears
) {}
