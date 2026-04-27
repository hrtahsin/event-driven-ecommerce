package com.eventdriven.platform.order.config;

import java.util.List;
import java.util.UUID;

public record JwtAuthenticatedUser(
        UUID userId,
        String email,
        List<String> roles
) {

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
