package com.eventdriven.platform.order.outbox;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OutboxPublisherJob {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxPublishingService outboxPublishingService;
    private final OutboxPublisherProperties properties;

    public OutboxPublisherJob(OutboxEventRepository outboxEventRepository,
                              OutboxPublishingService outboxPublishingService,
                              OutboxPublisherProperties properties) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxPublishingService = outboxPublishingService;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay}")
    public void publishPendingEvents() {
        if (!properties.isEnabled()) {
            return;
        }

        List<OutboxEventEntity> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.PENDING,
                PageRequest.of(0, properties.getBatchSize())
        );
        events.stream()
                .map(OutboxEventEntity::getId)
                .forEach(this::publishEvent);
    }

    private void publishEvent(UUID eventId) {
        try {
            outboxPublishingService.publishPendingEvent(eventId);
        } catch (OutboxPublishException exception) {
            // Leave the row PENDING; the next scheduled run will retry it.
        }
    }
}
