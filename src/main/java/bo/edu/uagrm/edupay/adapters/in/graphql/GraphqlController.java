package bo.edu.uagrm.edupay.adapters.in.graphql;

import bo.edu.uagrm.edupay.application.dto.*;
import bo.edu.uagrm.edupay.application.port.in.BiUseCase;
import bo.edu.uagrm.edupay.application.port.in.DocumentUseCase;
import bo.edu.uagrm.edupay.application.port.in.ErpUseCase;
import bo.edu.uagrm.edupay.application.service.AuthApplicationService;
import bo.edu.uagrm.edupay.domain.model.DocumentReviewStatus;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import graphql.GraphQLError;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;

@Controller
public class GraphqlController {
    private final ErpUseCase erpUseCase;
    private final DocumentUseCase documentUseCase;
    private final BiUseCase biUseCase;
    private final AuthApplicationService authService;

    private final Sinks.Many<Map<String, Object>> paymentSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Map<String, Object>> documentSink = Sinks.many().multicast().onBackpressureBuffer();

    public GraphqlController(ErpUseCase erpUseCase, DocumentUseCase documentUseCase, BiUseCase biUseCase,
            AuthApplicationService authService) {
        this.erpUseCase = erpUseCase;
        this.documentUseCase = documentUseCase;
        this.biUseCase = biUseCase;
        this.authService = authService;
    }

    @QueryMapping
    public FamilyFinancialStatusView familyFinancialStatus(@Argument Long familyId) {
        return erpUseCase.familyFinancialStatus(familyId);
    }

    @QueryMapping
    public List<StudentArrearsView> studentsInArrears(@Argument Integer limit) {
        return erpUseCase.studentsInArrears(limit == null ? 50 : limit);
    }

    @QueryMapping
    public CollectionDashboardView collectionDashboard(@Argument int year, @Argument int month) {
        return biUseCase.collectionDashboard(year, month);
    }

    @QueryMapping
    public DelinquencyDashboardView delinquencyDashboard(@Argument int year, @Argument int month) {
        return biUseCase.delinquencyDashboard(year, month);
    }

    @QueryMapping
    public List<DocumentView> listDocuments(@Argument String status) {
        return documentUseCase.listDocuments(status);
    }

    @QueryMapping
    public List<bo.edu.uagrm.edupay.application.dto.FamilyView> listFamilies() {
        return erpUseCase.listFamilies();
    }

    @MutationMapping
    public LoginResponse login(@Argument LoginRequestInput input) {
        return authService.login(new LoginRequest(input.email(), input.password()));
    }

    @MutationMapping
    public Map<String, Object> registerFamily(@Argument RegisterFamilyInput input) {
        Long id = erpUseCase
                .registerFamily(new RegisterFamilyCommand(input.externalId(), input.tutorName(), input.tutorEmail()));
        return Map.of("id", id, "externalId", input.externalId(), "tutorName", input.tutorName(), "tutorEmail",
                input.tutorEmail(), "active", true);
    }

    @MutationMapping
    public Map<String, Object> assignDiscount(@Argument AssignDiscountInput input) {
        Long id = erpUseCase
                .assignDiscount(new AssignDiscountCommand(input.familyId(), input.discountCode(), input.percentage()));
        return Map.of("id", id, "familyId", input.familyId(), "discountCode", input.discountCode(), "percentage",
                input.percentage());
    }

    @MutationMapping
    public Map<String, Object> reviewDocument(@Argument ReviewDocumentInput input) {
        Long id = documentUseCase.reviewDocument(new ReviewDocumentCommand(
                input.documentId(),
                input.reviewerUser(),
                DocumentReviewStatus.valueOf(input.status().name()),
                input.reason()));

        Map<String, Object> event = Map.of("eventId", java.util.UUID.randomUUID().toString(), "documentId",
                input.documentId(), "status", input.status().name());
        documentSink.tryEmitNext(event);

        return Map.of("id", id, "documentId", input.documentId(), "reviewerUser", input.reviewerUser(), "status",
                input.status().name(), "reason", input.reason());
    }

    @MutationMapping
    public DocumentView registerDocument(@Argument RegisterDocumentInput input) {
        return documentUseCase.registerDocument(
                Long.parseLong(input.familyId()),
                input.documentType(),
                input.storageKey(),
                input.uploadedBy());
    }

    @MutationMapping
    public Map<String, Object> issueInvoice(@Argument IssueInvoiceInput input) {
        Long id = documentUseCase
                .issueInvoice(new IssueInvoiceCommand(input.familyId(), input.periodCode(), input.amount()));
        return Map.of("id", id, "familyId", input.familyId(), "periodCode", input.periodCode(), "amount",
                input.amount(), "storageKey", "invoices/" + input.familyId() + "/" + input.periodCode() + ".pdf");
    }

    @SubscriptionMapping
    public Flux<Map<String, Object>> paymentConfirmed() {
        return paymentSink.asFlux();
    }

    @SubscriptionMapping
    public Flux<Map<String, Object>> documentStatusChanged() {
        return documentSink.asFlux();
    }

    public void emitPaymentEvent(String eventId, String paymentExternalId, String method, double amount) {
        paymentSink.tryEmitNext(
                Map.of("eventId", eventId, "paymentExternalId", paymentExternalId, "method", method, "amount", amount));
    }

    public record RegisterFamilyInput(String externalId, String tutorName, String tutorEmail) {
    }

    public record AssignDiscountInput(Long familyId, String discountCode, double percentage) {
    }

    public record ReviewDocumentInput(Long documentId, String reviewerUser, DocumentReviewStatus status,
            String reason) {
    }

    public record IssueInvoiceInput(Long familyId, String periodCode, double amount) {
    }

    public record LoginRequestInput(String email, String password) {
    }

    public record RegisterDocumentInput(String familyId, String documentType, String storageKey, String uploadedBy) {
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(Throwable ex) {
        return GraphQLError.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.getMessage() != null ? ex.getMessage() : "Unknown error")
                .build();
    }
}
