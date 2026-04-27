package com.eventdriven.platform.order.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID customerId,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency,

        @NotEmpty
        List<@Valid CreateOrderItemRequest> items
) {
}
