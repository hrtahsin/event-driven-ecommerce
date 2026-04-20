package com.eventdriven.platform.payment.api;

import java.util.List;

public record PaymentServiceInfoResponse(
        String serviceName,
        String boundedContext,
        List<String> ownedTables,
        List<String> synchronousApis,
        List<String> domainEventsPublished,
        List<String> domainEventsConsumed
) {
}
