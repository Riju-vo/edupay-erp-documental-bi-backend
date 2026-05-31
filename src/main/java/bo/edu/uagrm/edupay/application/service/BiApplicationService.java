package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.DelinquencySnapshotEntity;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.CollectionFactDailyJpaRepository;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.DelinquencySnapshotJpaRepository;
import bo.edu.uagrm.edupay.application.dto.CollectionByMethodView;
import bo.edu.uagrm.edupay.application.dto.CollectionDashboardView;
import bo.edu.uagrm.edupay.application.dto.DelinquencyDashboardView;
import bo.edu.uagrm.edupay.application.port.in.BiUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class BiApplicationService implements BiUseCase {
    private final CollectionFactDailyJpaRepository collectionRepository;
    private final DelinquencySnapshotJpaRepository delinquencyRepository;

    public BiApplicationService(CollectionFactDailyJpaRepository collectionRepository,
            DelinquencySnapshotJpaRepository delinquencyRepository) {
        this.collectionRepository = collectionRepository;
        this.delinquencyRepository = delinquencyRepository;
    }

    @Override
    public CollectionDashboardView collectionDashboard(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        var byMethod = collectionRepository.sumByMethod(from, to).stream()
                .map(r -> new CollectionByMethodView((String) r[0], ((java.math.BigDecimal) r[1]).doubleValue()))
                .toList();
        double total = collectionRepository.totalBetween(from, to).doubleValue();
        return new CollectionDashboardView(year, month, total, byMethod);
    }

    @Override
    public DelinquencyDashboardView delinquencyDashboard(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        DelinquencySnapshotEntity snapshot = delinquencyRepository
                .findTopBySnapshotDateBetweenOrderBySnapshotDateDesc(from, to)
                .orElseGet(() -> {
                    DelinquencySnapshotEntity empty = new DelinquencySnapshotEntity();
                    empty.setFamiliesInArrears(0);
                    empty.setTotalDebt(java.math.BigDecimal.ZERO);
                    return empty;
                });
        return new DelinquencyDashboardView(year, month, snapshot.getFamiliesInArrears(),
                snapshot.getTotalDebt().doubleValue());
    }
}
