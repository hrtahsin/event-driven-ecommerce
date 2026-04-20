package com.eventdriven.platform.common;

import com.eventdriven.platform.common.events.EventEnvelope;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventEnvelopeTest {

    @Test
    void shouldKeepEnvelopeMetadataIntact() {
        EventEnvelope<Map<String, Object>> envelope = new EventEnvelope<>(
                "evt-1",
                "OrderCreated",
                Instant.parse("2026-04-19T12:00:00Z"),
                "order-service",
                "corr-1",
                "order-123",
                Map.of("orderId", "order-123")
        );

        assertEquals("evt-1", envelope.eventId());
        assertEquals("OrderCreated", envelope.eventType());
        assertEquals("order-service", envelope.producer());
        assertEquals("order-123", envelope.aggregateId());
    }
}
