package com.eventdriven.platform.inventory.reservation;

import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class InventoryReservationServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("inventory_db")
            .withUsername("platform")
            .withPassword("platform");

    @Autowired
    private InventoryReservationService inventoryReservationService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private InventoryReservationRepository inventoryReservationRepository;

    @Test
    void shouldReserveInventoryOnlyOnceForDuplicateEvent() {
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        seedInventoryItem(productId, "CAM-1001", 5);
        OrderCreatedEvent event = orderCreated(orderId, productId, 2);

        inventoryReservationService.handleOrderCreated("evt-1", event);
        inventoryReservationService.handleOrderCreated("evt-1", event);

        InventoryItemEntity inventoryItem = inventoryItemRepository.findById(productId).orElseThrow();
        List<InventoryReservationEntity> reservations = inventoryReservationRepository.findByOrderId(orderId);

        assertEquals(3, inventoryItem.getAvailableQuantity());
        assertEquals(2, inventoryItem.getReservedQuantity());
        assertEquals(1, reservations.size());
        assertEquals(InventoryReservationStatus.RESERVED, reservations.getFirst().getStatus());
    }

    private void seedInventoryItem(UUID productId, String sku, int availableQuantity) {
        InventoryItemEntity item = new InventoryItemEntity();
        item.setProductId(productId);
        item.setSku(sku);
        item.setAvailableQuantity(availableQuantity);
        item.setReservedQuantity(0);
        item.setReorderThreshold(1);
        inventoryItemRepository.save(item);
    }

    private OrderCreatedEvent orderCreated(UUID orderId, UUID productId, int quantity) {
        return new OrderCreatedEvent(
                orderId,
                UUID.randomUUID(),
                new BigDecimal("799.99"),
                "USD",
                List.of(new OrderCreatedEvent.Item(
                        productId,
                        "CAM-1001",
                        "Camera Kit",
                        quantity,
                        new BigDecimal("799.99"),
                        new BigDecimal("799.99")
                )),
                Instant.now()
        );
    }
}
