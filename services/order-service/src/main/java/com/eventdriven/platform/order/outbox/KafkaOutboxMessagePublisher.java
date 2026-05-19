package com.eventdriven.platform.order.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxPublisherProperties properties;

    public KafkaOutboxMessagePublisher(KafkaTemplate<String, String> kafkaTemplate,
                                       OutboxPublisherProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(OutboxEventEntity event) {
        try {
            kafkaTemplate.send(event.getEventType(), event.getAggregateId(), event.getPayload().toString())
                    .get(properties.getSendTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            throw new OutboxPublishException("Failed to publish outbox event " + event.getEventId(), exception);
        }
    }
}
