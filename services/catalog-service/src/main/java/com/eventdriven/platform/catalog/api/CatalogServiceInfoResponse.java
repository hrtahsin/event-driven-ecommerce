package com.eventdriven.platform.catalog.api;

import java.util.List;

public record CatalogServiceInfoResponse(
        String serviceName,
        String boundedContext,
        List<String> ownedTables,
        List<String> synchronousApis,
        List<String> domainEventsPublished,
        List<String> domainEventsConsumed
) {
}
