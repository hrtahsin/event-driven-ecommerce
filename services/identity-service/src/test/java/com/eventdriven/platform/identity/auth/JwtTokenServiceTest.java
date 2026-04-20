package com.eventdriven.platform.identity.auth;

import com.eventdriven.platform.identity.config.JwtProperties;
import com.eventdriven.platform.identity.domain.RoleEntity;
import com.eventdriven.platform.identity.domain.RoleName;
import com.eventdriven.platform.identity.domain.UserEntity;
import com.eventdriven.platform.identity.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    @Test
    void shouldGenerateAndParseAccessToken() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("identity-service");
        jwtProperties.setSecret("change-me-phase-2-shared-jwt-secret-1234567890");

        JwtTokenService jwtTokenService = new JwtTokenService(jwtProperties);

        UserEntity user = new UserEntity();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("tester@example.com");
        user.setPasswordHash("hashed");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new LinkedHashSet<>());
        user.getRoles().add(new RoleEntity(null, RoleName.CUSTOMER));
        user.getRoles().add(new RoleEntity(null, RoleName.ADMIN));

        String token = jwtTokenService.generateToken(user);
        JwtAuthenticatedUser parsedUser = jwtTokenService.parse(token);

        assertEquals(userId, parsedUser.userId());
        assertEquals("tester@example.com", parsedUser.email());
        assertTrue(parsedUser.roles().contains("ADMIN"));
        assertTrue(parsedUser.roles().contains("CUSTOMER"));
    }
}
