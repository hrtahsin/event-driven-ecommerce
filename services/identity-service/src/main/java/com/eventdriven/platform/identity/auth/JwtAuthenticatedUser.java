package com.eventdriven.platform.identity.auth;

import java.util.List;
import java.util.UUID;

public record JwtAuthenticatedUser(
        UUID userId,
        String email,
        List<String> roles
) {
}
