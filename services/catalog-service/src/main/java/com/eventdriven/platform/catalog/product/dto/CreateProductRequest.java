package com.eventdriven.platform.catalog.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record CreateProductRequest(
        @NotBlank
        @Size(max = 50)
        String sku,
        @NotBlank
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description,
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price,
        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "currency must be a 3-letter code")
        String currency,
        Boolean active,
        Set<@NotBlank @Size(max = 120) String> categories
) {
}
