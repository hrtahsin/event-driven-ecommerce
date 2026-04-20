package com.eventdriven.platform.common.events;

public final class TopicNames {

    public static final String ORDER_CREATED = "order.created";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_REJECTED = "inventory.rejected";
    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String INVENTORY_RELEASED = "inventory.released";
    public static final String STOCK_LOW = "stock.low";
    public static final String NOTIFICATION_REQUESTED = "notification.requested";

    private TopicNames() {
    }
}
