package com.eventdriven.platform.inventory.reservation;

import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InventoryReservationService {

    private static final String CONSUMER_GROUP = "inventory-service";

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final ProcessedEventRepository processedEventRepository;

    public InventoryReservationService(InventoryItemRepository inventoryItemRepository,
                                       InventoryReservationRepository inventoryReservationRepository,
                                       ProcessedEventRepository processedEventRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void handleOrderCreated(String eventId, OrderCreatedEvent event) {
        if (processedEventRepository.existsByEventIdAndConsumerGroup(eventId, CONSUMER_GROUP)) {
            return;
        }

        aggregateQuantitiesByProduct(event).forEach((productId, quantity) ->
                reserveOrderItem(event.orderId(), productId, quantity)
        );
        processedEventRepository.save(new ProcessedEventEntity(eventId, CONSUMER_GROUP));
    }

    private void reserveOrderItem(UUID orderId, UUID productId, int quantity) {
        InventoryReservationEntity reservation = inventoryItemRepository.findByProductId(productId)
                .filter(item -> item.canReserve(quantity))
                .map(item -> {
                    item.reserve(quantity);
                    return InventoryReservationEntity.reserved(orderId, productId, quantity);
                })
                .orElseGet(() -> InventoryReservationEntity.rejected(orderId, productId, quantity));

        inventoryReservationRepository.save(reservation);
    }

    private Map<UUID, Integer> aggregateQuantitiesByProduct(OrderCreatedEvent event) {
        Map<UUID, Integer> quantitiesByProduct = new LinkedHashMap<>();
        event.items().forEach(item -> quantitiesByProduct.merge(
                item.productId(),
                item.quantity(),
                Integer::sum
        ));
        return quantitiesByProduct;
    }
}
