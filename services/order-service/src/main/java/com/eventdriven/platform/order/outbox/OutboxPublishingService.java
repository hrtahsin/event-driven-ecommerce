package com.eventdriven.platform.order.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxPublishingService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxMessagePublisher outboxMessagePublisher;

    public OutboxPublishingService(OutboxEventRepository outboxEventRepository,
                                   OutboxMessagePublisher outboxMessagePublisher) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
    }

    @Transactional
    public void publishPendingEvent(UUID eventId) {
        OutboxEventEntity event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + eventId));
        if (event.getStatus() != OutboxEventStatus.PENDING) {
            return;
        }

        publishEvent(event);
    }

    void publishEvent(OutboxEventEntity event) {
        if (event.getStatus() != OutboxEventStatus.PENDING) {
            return;
        }
        outboxMessagePublisher.publish(event);
        event.markPublished(Instant.now());
    }
}
