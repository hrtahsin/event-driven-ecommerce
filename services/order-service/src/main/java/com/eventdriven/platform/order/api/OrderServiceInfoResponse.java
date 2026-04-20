package com.eventdriven.platform.order.api;

import java.util.List;

public record OrderServiceInfoResponse(
        String serviceName,
        String boundedContext,
        List<String> ownedTables,
        List<String> synchronousApis,
        List<String> domainEventsPublished,
        List<String> domainEventsConsumed
) {
}
