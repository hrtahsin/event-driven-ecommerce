package com.eventdriven.platform.catalog.product.dto;

import java.util.List;

public record PagedProductsResponse(
        List<ProductSummaryResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
