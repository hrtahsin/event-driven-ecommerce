package com.eventdriven.platform.catalog.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record UpdateProductRequest(
        @Size(max = 50)
        String sku,
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @DecimalMin(value = "0.01")
        BigDecimal price,
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "currency must be a 3-letter code")
        String currency,
        Boolean active,
        Set<@Size(max = 120) String> categories
) {
}
