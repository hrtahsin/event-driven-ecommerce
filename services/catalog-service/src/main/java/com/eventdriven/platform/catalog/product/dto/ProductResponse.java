package com.eventdriven.platform.catalog.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currency,
        boolean active,
        List<String> categories,
        Instant createdAt,
        Instant updatedAt
) {
}
