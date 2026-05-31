package bo.edu.uagrm.edupay.application.service;

import bo.edu.uagrm.edupay.adapters.out.persistence.entity.CollectionFactDailyEntity;
import bo.edu.uagrm.edupay.adapters.out.persistence.entity.InboxEventEntity;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.CollectionFactDailyJpaRepository;
import bo.edu.uagrm.edupay.adapters.out.persistence.repo.InboxEventJpaRepository;
import bo.edu.uagrm.edupay.application.dto.PaymentConfirmedEvent;
import bo.edu.uagrm.edupay.application.port.in.IntegrationUseCase;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class IntegrationApplicationService implements IntegrationUseCase {
    private final InboxEventJpaRepository inboxRepository;
    private final CollectionFactDailyJpaRepository collectionRepository;

    public IntegrationApplicationService(InboxEventJpaRepository inboxRepository,
                                         CollectionFactDailyJpaRepository collectionRepository) {
        this.inboxRepository = inboxRepository;
        this.collectionRepository = collectionRepository;
    }

    @Override
    @Transactional
    public boolean processPaymentConfirmed(PaymentConfirmedEvent event) {
        if (inboxRepository.existsByEventId(event.eventId())) {
            return false;
        }

        InboxEventEntity inbox = new InboxEventEntity();
        inbox.setEventId(event.eventId());
        inbox.setEventType("PaymentConfirmed");
        inbox.setPayload("{\"paymentExternalId\":\"" + event.paymentExternalId() + "\"}");
        inboxRepository.save(inbox);

        CollectionFactDailyEntity fact = new CollectionFactDailyEntity();
        fact.setBusinessDate(LocalDate.now());
        fact.setPaymentMethod(event.paymentMethod());
        fact.setAmount(BigDecimal.valueOf(event.amount()));
        fact.setExternalPaymentId(event.paymentExternalId());
        collectionRepository.save(fact);

        return true;
    }

    @Override
    @Transactional
    public boolean processPaymentReversed(bo.edu.uagrm.edupay.application.dto.PaymentReversedEvent event) {
        if (inboxRepository.existsByEventId(event.eventId())) {
            return false;
        }

        InboxEventEntity inbox = new InboxEventEntity();
        inbox.setEventId(event.eventId());
        inbox.setEventType("PaymentReversed");
        inbox.setPayload("{\"paymentExternalId\":\"" + event.paymentExternalId() + "\",\"reason\":\"" + event.reason() + "\"}");
        inboxRepository.save(inbox);

        // TODO: In a full implementation, we should reverse the CollectionFactDailyEntity
        // and update AccountStatusEntity to IN_ARREARS. 
        // For now, we ensure the event is ingested idempotently.

        return true;
    }
}
