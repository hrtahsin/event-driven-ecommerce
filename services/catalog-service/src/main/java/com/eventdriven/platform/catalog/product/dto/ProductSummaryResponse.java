package com.eventdriven.platform.catalog.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        String sku,
        String name,
        BigDecimal price,
        String currency,
        boolean active,
        List<String> categories
) {
}
