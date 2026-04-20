package com.eventdriven.platform.order.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class OrderServiceInfoController {

    @GetMapping
    public OrderServiceInfoResponse describe() {
        return new OrderServiceInfoResponse(
                "order-service",
                "Order lifecycle and outbox publishing",
                List.of("orders", "order_items", "outbox_events", "processed_events"),
                List.of("POST /orders", "GET /orders/{id}", "GET /orders/mine"),
                List.of("order.created", "order.confirmed", "order.cancelled"),
                List.of("inventory.reserved", "inventory.rejected", "payment.succeeded", "payment.failed")
        );
    }
}
