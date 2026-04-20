package com.eventdriven.platform.inventory.api;

import java.util.List;

public record InventoryServiceInfoResponse(
        String serviceName,
        String boundedContext,
        List<String> ownedTables,
        List<String> synchronousApis,
        List<String> domainEventsPublished,
        List<String> domainEventsConsumed
) {
}
