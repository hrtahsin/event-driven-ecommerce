package com.eventdriven.platform.identity.auth;

import com.eventdriven.platform.identity.config.JwtProperties;
import com.eventdriven.platform.identity.domain.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserEntity user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getAccessTokenTtl());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim("uid", user.getId().toString())
                .claim("roles", user.getRoles().stream().map(role -> role.getName().name()).sorted().toList())
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
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

    public long getAccessTokenTtlSeconds() {
        return jwtProperties.getAccessTokenTtl().toSeconds();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
