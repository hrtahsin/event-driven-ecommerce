package com.eventdriven.platform.payment.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class PaymentServiceInfoController {

    @GetMapping
    public PaymentServiceInfoResponse describe() {
        return new PaymentServiceInfoResponse(
                "payment-service",
                "Payment simulation and payment outcome publishing",
                List.of("payments", "payment_attempts", "outbox_events", "processed_events"),
                List.of(),
                List.of("payment.succeeded", "payment.failed"),
                List.of("inventory.reserved")
        );
    }
}
