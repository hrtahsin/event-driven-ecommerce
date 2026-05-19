package com.eventdriven.platform.inventory.reservation;

import com.eventdriven.platform.common.events.EventEnvelope;
import com.eventdriven.platform.common.events.TopicNames;
import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryReservationService inventoryReservationService;
    private final JavaType envelopeType;

    public OrderCreatedConsumer(ObjectMapper objectMapper,
                                InventoryReservationService inventoryReservationService) {
        this.objectMapper = objectMapper;
        this.inventoryReservationService = inventoryReservationService;
        this.envelopeType = objectMapper.getTypeFactory()
                .constructParametricType(EventEnvelope.class, OrderCreatedEvent.class);
    }

    @KafkaListener(topics = TopicNames.ORDER_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void handle(String message) {
        EventEnvelope<OrderCreatedEvent> envelope = readEnvelope(message);
        inventoryReservationService.handleOrderCreated(envelope.eventId(), envelope.payload());
    }

    private EventEnvelope<OrderCreatedEvent> readEnvelope(String message) {
        try {
            return objectMapper.readValue(message, envelopeType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid order.created event payload", exception);
        }
    }
}
