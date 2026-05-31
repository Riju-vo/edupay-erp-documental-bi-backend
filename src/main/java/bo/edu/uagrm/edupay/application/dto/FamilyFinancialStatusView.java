package bo.edu.uagrm.edupay.application.dto;

public record FamilyFinancialStatusView(
        Long familyId,
        String externalId,
        String tutorName,
        String tutorEmail,
        boolean active,
        double totalDebt,
        Double riskScore,
        int monthsPaid,
        int monthsPending,
        int monthsInArrears
) {
}
