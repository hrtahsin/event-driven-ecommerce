package com.eventdriven.platform.order.orders;

import com.eventdriven.platform.order.config.JwtAuthenticatedUser;
import com.eventdriven.platform.order.domain.OrderEntity;
import com.eventdriven.platform.order.domain.OrderItemEntity;
import com.eventdriven.platform.order.domain.OrderRepository;
import com.eventdriven.platform.order.domain.OrderStatus;
import com.eventdriven.platform.order.orders.dto.CreateOrderItemRequest;
import com.eventdriven.platform.order.orders.dto.CreateOrderRequest;
import com.eventdriven.platform.order.orders.dto.OrderItemResponse;
import com.eventdriven.platform.order.orders.dto.OrderResponse;
import com.eventdriven.platform.order.orders.dto.PagedOrdersResponse;
import com.eventdriven.platform.order.support.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(UUID customerId, CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setCustomerId(customerId);
        order.setCurrency(normalizeCurrency(request.currency()));
        order.setStatus(OrderStatus.CREATED);

        request.items().stream()
                .map(this::toOrderItem)
                .forEach(order::addItem);
        order.recalculateTotal();

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(JwtAuthenticatedUser user, UUID orderId) {
        OrderEntity order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        assertCanRead(user, order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public PagedOrdersResponse listCustomerOrders(UUID customerId, Pageable pageable) {
        Page<OrderEntity> page = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
        return new PagedOrdersResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private OrderItemEntity toOrderItem(CreateOrderItemRequest request) {
        BigDecimal unitPrice = money(request.unitPrice());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(request.quantity()));

        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(request.productId());
        item.setSku(normalizeSku(request.sku()));
        item.setProductName(normalizeProductName(request.productName()));
        item.setQuantity(request.quantity());
        item.setUnitPrice(unitPrice);
        item.setLineTotal(money(lineTotal));
        return item;
    }

    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getItems().stream().map(this::toItemResponse).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItemEntity item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase();
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    private String normalizeProductName(String productName) {
        return productName.trim();
    }

    private void assertCanRead(JwtAuthenticatedUser user, OrderEntity order) {
        if (user.hasRole("ADMIN") || order.getCustomerId().equals(user.userId())) {
            return;
        }
        throw new AccessDeniedException("Order does not belong to the authenticated user");
    }
}
