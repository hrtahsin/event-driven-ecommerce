package com.eventdriven.platform.order.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CatalogProductResponse(
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

    ProductSnapshot toSnapshot() {
        return new ProductSnapshot(id, sku, name, price, currency, active);
    }
}
