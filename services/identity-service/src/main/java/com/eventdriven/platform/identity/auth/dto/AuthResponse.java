package com.eventdriven.platform.identity.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UserProfileResponse user
) {
}
