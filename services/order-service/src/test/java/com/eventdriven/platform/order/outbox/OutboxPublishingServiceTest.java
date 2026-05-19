package com.eventdriven.platform.order.outbox;

import com.eventdriven.platform.common.events.TopicNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutboxPublishingServiceTest {

    @Test
    void shouldMarkEventPublishedAfterSuccessfulSend() {
        CapturingPublisher publisher = new CapturingPublisher();
        OutboxPublishingService service = new OutboxPublishingService(null, publisher);
        OutboxEventEntity event = outboxEvent();

        service.publishEvent(event);

        assertEquals(event, publisher.publishedEvent);
        assertEquals(OutboxEventStatus.PUBLISHED, event.getStatus());
        assertNotNull(event.getPublishedAt());
    }

    @Test
    void shouldLeaveEventPendingWhenSendFails() {
        OutboxPublishingService service = new OutboxPublishingService(null, event -> {
            throw new OutboxPublishException("send failed", new RuntimeException("broker unavailable"));
        });
        OutboxEventEntity event = outboxEvent();

        assertThrows(OutboxPublishException.class, () -> service.publishEvent(event));

        assertEquals(OutboxEventStatus.PENDING, event.getStatus());
        assertNull(event.getPublishedAt());
    }

    private OutboxEventEntity outboxEvent() {
        OutboxEventEntity event = new OutboxEventEntity();
        event.setAggregateType("Order");
        event.setAggregateId(UUID.randomUUID().toString());
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(TopicNames.ORDER_CREATED);
        event.setPayload(new ObjectMapper().createObjectNode().put("eventType", TopicNames.ORDER_CREATED));
        event.setStatus(OutboxEventStatus.PENDING);
        return event;
    }

    private static class CapturingPublisher implements OutboxMessagePublisher {

        private OutboxEventEntity publishedEvent;

        @Override
        public void publish(OutboxEventEntity event) {
            publishedEvent = event;
        }
    }
}
