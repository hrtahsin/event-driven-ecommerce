package com.eventdriven.platform.order.outbox;

public interface OutboxMessagePublisher {

    void publish(OutboxEventEntity event);
}
