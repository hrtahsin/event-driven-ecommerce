package com.eventdriven.platform.order.outbox;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisherJob {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxMessagePublisher outboxMessagePublisher;
    private final OutboxPublisherProperties properties;

    public OutboxPublisherJob(OutboxEventRepository outboxEventRepository,
                              OutboxMessagePublisher outboxMessagePublisher,
                              OutboxPublisherProperties properties) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay}")
    @Transactional
    public void publishPendingEvents() {
        if (!properties.isEnabled()) {
            return;
        }

        List<OutboxEventEntity> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.PENDING,
                PageRequest.of(0, properties.getBatchSize())
        );
        events.forEach(outboxMessagePublisher::publish);
    }
}
