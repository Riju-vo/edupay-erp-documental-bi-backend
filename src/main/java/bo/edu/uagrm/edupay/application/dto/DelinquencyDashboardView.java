package bo.edu.uagrm.edupay.application.dto;

public record DelinquencyDashboardView(int year, int month, int familiesInArrears, double totalDebt) {}
