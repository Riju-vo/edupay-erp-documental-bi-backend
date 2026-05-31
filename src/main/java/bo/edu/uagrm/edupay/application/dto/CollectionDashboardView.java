package bo.edu.uagrm.edupay.application.dto;

import java.util.List;

public record CollectionDashboardView(int year, int month, double totalCollected, List<CollectionByMethodView> byMethod) {}
