package com.eventdriven.platform.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventEnvelope<T>(
        String eventId,
        String eventType,
        Instant occurredAt,
        String producer,
        String correlationId,
        String aggregateId,
        T payload
) {
}
