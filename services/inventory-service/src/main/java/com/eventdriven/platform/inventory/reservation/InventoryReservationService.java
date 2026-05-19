package com.eventdriven.platform.inventory.reservation;

import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InventoryReservationService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryReservationRepository inventoryReservationRepository;

    public InventoryReservationService(InventoryItemRepository inventoryItemRepository,
                                       InventoryReservationRepository inventoryReservationRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryReservationRepository = inventoryReservationRepository;
    }

    @Transactional
    public void handleOrderCreated(String eventId, OrderCreatedEvent event) {
        aggregateQuantitiesByProduct(event).forEach((productId, quantity) ->
                reserveOrderItem(event.orderId(), productId, quantity)
        );
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
