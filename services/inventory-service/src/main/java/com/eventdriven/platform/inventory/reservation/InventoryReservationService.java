package com.eventdriven.platform.inventory.reservation;

import com.eventdriven.platform.common.events.order.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class InventoryReservationService {

    public void handleOrderCreated(String eventId, OrderCreatedEvent event) {
        // Persistence is added in the next commit; this seam keeps the Kafka consumer focused on transport.
    }
}
