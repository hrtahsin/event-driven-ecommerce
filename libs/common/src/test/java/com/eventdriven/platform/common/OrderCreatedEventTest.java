package com.eventdriven.platform.common;

import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderCreatedEventTest {

    @Test
    void shouldCarryOrderSnapshotForDownstreamConsumers() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-18T10:00:00Z");

        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                customerId,
                new BigDecimal("1599.98"),
                "USD",
                List.of(new OrderCreatedEvent.Item(
                        productId,
                        "CAM-1001",
                        "Camera Kit",
                        2,
                        new BigDecimal("799.99"),
                        new BigDecimal("1599.98")
                )),
                createdAt
        );

        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals("USD", event.currency());
        assertEquals(productId, event.items().getFirst().productId());
        assertEquals(createdAt, event.createdAt());
    }
}
