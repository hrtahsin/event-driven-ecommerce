package com.eventdriven.platform.catalog.config;

import java.util.List;
import java.util.UUID;

public record JwtAuthenticatedUser(
        UUID userId,
        String email,
        List<String> roles
) {
}
