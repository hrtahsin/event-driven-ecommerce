package com.eventdriven.platform.notification.api;

import java.util.List;

public record NotificationServiceInfoResponse(
        String serviceName,
        String boundedContext,
        List<String> ownedTables,
        List<String> synchronousApis,
        List<String> domainEventsPublished,
        List<String> domainEventsConsumed
) {
}
