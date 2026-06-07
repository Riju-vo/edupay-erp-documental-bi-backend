package bo.edu.uagrm.edupay.application.port.in;

import bo.edu.uagrm.edupay.application.dto.DocumentView;
import bo.edu.uagrm.edupay.application.dto.IssueInvoiceCommand;
import bo.edu.uagrm.edupay.application.dto.ReviewDocumentCommand;

import java.util.List;

public interface DocumentUseCase {
    Long reviewDocument(ReviewDocumentCommand command);

    Long issueInvoice(IssueInvoiceCommand command);

    List<DocumentView> listDocuments(String status);

    DocumentView registerDocument(Long familyId, String documentType, String storageKey, String uploadedBy);
}
