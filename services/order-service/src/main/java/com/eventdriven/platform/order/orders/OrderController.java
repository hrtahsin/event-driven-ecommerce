package com.eventdriven.platform.order.orders;

import com.eventdriven.platform.order.config.JwtAuthenticatedUser;
import com.eventdriven.platform.order.orders.dto.CreateOrderRequest;
import com.eventdriven.platform.order.orders.dto.OrderResponse;
import com.eventdriven.platform.order.orders.dto.PagedOrdersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create an order")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal JwtAuthenticatedUser user,
                                                     @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(user.userId(), request));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by id")
    public OrderResponse getOrder(@AuthenticationPrincipal JwtAuthenticatedUser user,
                                  @PathVariable UUID orderId) {
        return orderService.getOrder(user, orderId);
    }

    @GetMapping("/mine")
    @Operation(summary = "List orders for a customer")
    public PagedOrdersResponse listCustomerOrders(@AuthenticationPrincipal JwtAuthenticatedUser user,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        return orderService.listCustomerOrders(user.userId(), pageable);
    }
}
