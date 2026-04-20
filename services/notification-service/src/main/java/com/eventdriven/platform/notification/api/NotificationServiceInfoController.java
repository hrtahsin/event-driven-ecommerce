package com.eventdriven.platform.notification.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class NotificationServiceInfoController {

    @GetMapping
    public NotificationServiceInfoResponse describe() {
        return new NotificationServiceInfoResponse(
                "notification-service",
                "Notification audit and delivery simulation",
                List.of("notifications", "delivery_attempts", "processed_events"),
                List.of(),
                List.of("notification.requested"),
                List.of("order.confirmed", "order.cancelled", "inventory.rejected", "payment.failed", "payment.succeeded")
        );
    }
}
