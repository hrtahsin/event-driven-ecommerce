package com.eventdriven.platform.order.outbox;

import com.eventdriven.platform.common.events.EventEnvelope;
import com.eventdriven.platform.common.events.TopicNames;
import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import com.eventdriven.platform.order.domain.OrderEntity;
import com.eventdriven.platform.order.domain.OrderItemEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderOutboxService {

    private static final String PRODUCER = "order-service";
    private static final String ORDER_AGGREGATE = "Order";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderOutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public void saveOrderCreated(OrderEntity order) {
        String eventId = UUID.randomUUID().toString();
        OrderCreatedEvent payload = toOrderCreatedEvent(order);
        EventEnvelope<OrderCreatedEvent> envelope = new EventEnvelope<>(
                eventId,
                TopicNames.ORDER_CREATED,
                Instant.now(),
                PRODUCER,
                null,
                order.getId().toString(),
                payload
        );

        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.setAggregateType(ORDER_AGGREGATE);
        outboxEvent.setAggregateId(order.getId().toString());
        outboxEvent.setEventId(eventId);
        outboxEvent.setEventType(TopicNames.ORDER_CREATED);
        outboxEvent.setPayload(objectMapper.valueToTree(envelope));
        outboxEvent.setStatus(OutboxEventStatus.PENDING);

        outboxEventRepository.save(outboxEvent);
    }

    private OrderCreatedEvent toOrderCreatedEvent(OrderEntity order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getItems().stream()
                        .map(this::toOrderCreatedItem)
                        .toList(),
                order.getCreatedAt()
        );
    }

    private OrderCreatedEvent.Item toOrderCreatedItem(OrderItemEntity item) {
        return new OrderCreatedEvent.Item(
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
