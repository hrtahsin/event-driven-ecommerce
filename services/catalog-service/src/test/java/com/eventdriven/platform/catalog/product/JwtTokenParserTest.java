package com.eventdriven.platform.catalog.product;

import com.eventdriven.platform.catalog.config.JwtAuthenticatedUser;
import com.eventdriven.platform.catalog.config.JwtProperties;
import com.eventdriven.platform.catalog.config.JwtTokenParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenParserTest {

    @Test
    void shouldParseAdminTokenClaims() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("identity-service");
        jwtProperties.setSecret("change-me-phase-2-shared-jwt-secret-1234567890");
        JwtTokenParser jwtTokenParser = new JwtTokenParser(jwtProperties);

        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("admin@eventdriven.local")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .claim("uid", userId.toString())
                .claim("roles", List.of("ADMIN"))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        JwtAuthenticatedUser parsed = jwtTokenParser.parse(token);

        assertEquals(userId, parsed.userId());
        assertEquals("admin@eventdriven.local", parsed.email());
        assertTrue(parsed.roles().contains("ADMIN"));
    }
}
