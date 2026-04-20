package com.eventdriven.platform.identity.auth.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String status,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
