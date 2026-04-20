package com.eventdriven.platform.inventory.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class InventoryServiceInfoController {

    @GetMapping
    public InventoryServiceInfoResponse describe() {
        return new InventoryServiceInfoResponse(
                "inventory-service",
                "Stock levels, reservations, and release workflow",
                List.of("inventory_items", "inventory_reservations", "inventory_movements", "outbox_events", "processed_events"),
                List.of("GET /inventory/{productId}", "PATCH /inventory/{productId}/restock"),
                List.of("inventory.reserved", "inventory.rejected", "inventory.released", "stock.low"),
                List.of("order.created", "payment.failed", "order.cancelled")
        );
    }
}
