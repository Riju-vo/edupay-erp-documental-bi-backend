package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.*;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.*;
import bo.edu.uagrm.edupay.application.dto.*;
import bo.edu.uagrm.edupay.application.port.in.ErpUseCase;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class ErpApplicationService implements ErpUseCase {
    private final FamilyJpaRepository familyRepository;
    private final AccountStatusJpaRepository accountStatusRepository;
    private final DiscountAssignmentJpaRepository discountRepository;
    private final AuditLogJpaRepository auditRepository;

    public ErpApplicationService(FamilyJpaRepository familyRepository,
                                 AccountStatusJpaRepository accountStatusRepository,
                                 DiscountAssignmentJpaRepository discountRepository,
                                 AuditLogJpaRepository auditRepository) {
        this.familyRepository = familyRepository;
        this.accountStatusRepository = accountStatusRepository;
        this.discountRepository = discountRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public FamilyFinancialStatusView familyFinancialStatus(Long familyId) {
        FamilyEntity family = familyRepository.findById(familyId).orElseThrow();
        List<AccountStatusEntity> statuses = accountStatusRepository.findByFamily_Id(familyId);

        double totalDebt = statuses.stream().map(AccountStatusEntity::getDebtAmount).mapToDouble(BigDecimal::doubleValue).sum();
        int monthsPaid = (int) statuses.stream().filter(s -> "PAID".equalsIgnoreCase(s.getStatus())).count();
        int monthsInArrears = (int) statuses.stream().filter(s -> "IN_ARREARS".equalsIgnoreCase(s.getStatus())).count();
        int monthsPending = Math.max(0, statuses.size() - monthsPaid - monthsInArrears);

        FamilyView familyView = new FamilyView(
                family.getId(),
                family.getExternalId(),
                family.getTutorName(),
                family.getTutorEmail(),
                family.isActive()
        );
        return new FamilyFinancialStatusView(
                familyView,
                totalDebt,
                null,
                monthsPaid,
                monthsPending,
                monthsInArrears
        );
    }

    @Override
    public List<StudentArrearsView> studentsInArrears(int limit) {
        return accountStatusRepository.findAll().stream()
                .filter(s -> s.getDebtAmount().doubleValue() > 0)
                .sorted(Comparator.comparing(AccountStatusEntity::getDebtAmount).reversed())
                .limit(limit)
                .map(s -> new StudentArrearsView(
                        "FAMILY-" + s.getFamily().getId(),
                        s.getFamily().getTutorName(),
                        s.getDebtAmount().doubleValue()))
                .toList();
    }

    @Override
    @Transactional
    public Long registerFamily(RegisterFamilyCommand command) {
        FamilyEntity entity = new FamilyEntity();
        entity.setExternalId(command.externalId());
        entity.setTutorName(command.tutorName());
        entity.setTutorEmail(command.tutorEmail());
        familyRepository.save(entity);
        saveAudit("REGISTER_FAMILY", "ERP_FAMILY", entity.getId().toString(), "admin", "New family registered");
        return entity.getId();
    }

    @Override
    @Transactional
    public Long assignDiscount(AssignDiscountCommand command) {
        FamilyEntity family = familyRepository.findById(command.familyId()).orElseThrow();
        DiscountAssignmentEntity discount = new DiscountAssignmentEntity();
        discount.setFamily(family);
        discount.setDiscountCode(command.discountCode());
        discount.setPercentage(BigDecimal.valueOf(command.percentage()));
        discountRepository.save(discount);
        saveAudit("ASSIGN_DISCOUNT", "ERP_FAMILY", family.getId().toString(), "admin", command.discountCode());
        return discount.getId();
    }

    @Override
    public List<FamilyView> listFamilies() {
        return familyRepository.findAll().stream()
                .map(f -> new FamilyView(f.getId(), f.getExternalId(), f.getTutorName(), f.getTutorEmail(), f.isActive()))
                .toList();
    }

    private void saveAudit(String action, String entityType, String entityId, String actor, String details) {
        AuditLogEntity log = new AuditLogEntity();
        log.setActionCode(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActor(actor);
        log.setDetails(details);
        auditRepository.save(log);
    }
}
