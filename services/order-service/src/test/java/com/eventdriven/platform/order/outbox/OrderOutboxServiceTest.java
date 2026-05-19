package com.eventdriven.platform.order.outbox;

import com.eventdriven.platform.common.events.TopicNames;
import com.eventdriven.platform.order.domain.OrderEntity;
import com.eventdriven.platform.order.domain.OrderItemEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderOutboxServiceTest {

    private final OrderOutboxService orderOutboxService = new OrderOutboxService(
            null,
            new ObjectMapper().findAndRegisterModules()
    );

    @Test
    void shouldPersistOrderCreatedEnvelopeAsPendingOutboxEvent() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-18T10:00:00Z");
        OrderEntity order = order(orderId, customerId, productId, createdAt);

        OutboxEventEntity outboxEvent = orderOutboxService.toOrderCreatedOutboxEvent(order);
        JsonNode payload = outboxEvent.getPayload();

        assertEquals("Order", outboxEvent.getAggregateType());
        assertEquals(orderId.toString(), outboxEvent.getAggregateId());
        assertEquals(TopicNames.ORDER_CREATED, outboxEvent.getEventType());
        assertEquals(OutboxEventStatus.PENDING, outboxEvent.getStatus());
        assertEquals(orderId.toString(), payload.get("aggregateId").asText());
        assertEquals(TopicNames.ORDER_CREATED, payload.get("eventType").asText());
        assertEquals(orderId.toString(), payload.at("/payload/orderId").asText());
        assertEquals(customerId.toString(), payload.at("/payload/customerId").asText());
        assertEquals("CAM-1001", payload.at("/payload/items/0/sku").asText());
    }

    private OrderEntity order(UUID orderId, UUID customerId, UUID productId, Instant createdAt) {
        OrderEntity order = new OrderEntity();
        ReflectionTestUtils.setField(order, "id", orderId);
        ReflectionTestUtils.setField(order, "createdAt", createdAt);
        order.setCustomerId(customerId);
        order.setCurrency("USD");

        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(productId);
        item.setSku("CAM-1001");
        item.setProductName("Camera Kit");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("799.99"));
        item.setLineTotal(new BigDecimal("1599.98"));

        order.addItem(item);
        order.recalculateTotal();
        return order;
    }
}
