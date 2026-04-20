package com.eventdriven.platform.catalog.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenParser {

    private final JwtProperties jwtProperties;

    public JwtTokenParser(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public JwtAuthenticatedUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            @SuppressWarnings("unchecked")
            List<String> roles = ((List<Object>) claims.get("roles"))
                    .stream()
                    .map(String::valueOf)
                    .toList();

            return new JwtAuthenticatedUser(
                    UUID.fromString(claims.get("uid", String.class)),
                    claims.getSubject(),
                    roles
            );
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtException("Invalid access token", exception);
        }
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
