package bo.edu.uagrm.edupay.application.port.in;

import bo.edu.uagrm.edupay.application.dto.IssueInvoiceCommand;
import bo.edu.uagrm.edupay.application.dto.ReviewDocumentCommand;

public interface DocumentUseCase {
    Long reviewDocument(ReviewDocumentCommand command);
    Long issueInvoice(IssueInvoiceCommand command);
}
