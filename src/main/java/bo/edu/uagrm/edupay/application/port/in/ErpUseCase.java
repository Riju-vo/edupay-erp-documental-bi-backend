package bo.edu.uagrm.edupay.application.port.in;

import bo.edu.uagrm.edupay.application.dto.*;
import bo.edu.uagrm.edupay.application.dto.FamilyView;

import java.util.List;

public interface ErpUseCase {
    FamilyFinancialStatusView familyFinancialStatus(Long familyId);
    List<StudentArrearsView> studentsInArrears(int limit);
    Long registerFamily(RegisterFamilyCommand command);
    Long assignDiscount(AssignDiscountCommand command);
    List<FamilyView> listFamilies();
}
