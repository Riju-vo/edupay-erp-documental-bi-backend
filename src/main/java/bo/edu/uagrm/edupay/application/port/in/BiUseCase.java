package bo.edu.uagrm.edupay.application.port.in;

import bo.edu.uagrm.edupay.application.dto.CollectionDashboardView;
import bo.edu.uagrm.edupay.application.dto.DelinquencyDashboardView;

public interface BiUseCase {
    CollectionDashboardView collectionDashboard(int year, int month);
    DelinquencyDashboardView delinquencyDashboard(int year, int month);
}
