package com.eventdriven.platform.order.orders.dto;

import java.util.List;

public record PagedOrdersResponse(
        List<OrderResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
