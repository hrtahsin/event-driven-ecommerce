package com.eventdriven.platform.catalog.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class CatalogServiceIntegrationTest {

    private static final String JWT_SECRET = "change-me-phase-2-shared-jwt-secret-1234567890";
    private static final String JWT_ISSUER = "identity-service";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("catalog_db")
            .withUsername("platform")
            .withPassword("platform");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRejectProductCreationWithoutAdminToken() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(
                                "SKU-401",
                                "Camera",
                                "Mirrorless body",
                                "799.99",
                                "USD",
                                true,
                                List.of("Electronics")
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateAndReadProductThroughPublicEndpoints() throws Exception {
        String token = adminToken();
        String sku = "CAM-" + System.nanoTime();

        String responseBody = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(
                                sku,
                                "Camera Kit",
                                "Mirrorless starter bundle",
                                "999.99",
                                "USD",
                                true,
                                List.of("Electronics", "Photography")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(sku))
                .andExpect(jsonPath("$.categories[0]").value("Electronics"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode product = objectMapper.readTree(responseBody);
        String productId = product.get("id").asText();

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Camera Kit"));

        mockMvc.perform(get("/products")
                        .param("search", sku.toLowerCase())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].sku").value(sku));
    }

    @Test
    void shouldUpdateProductWhenAdmin() throws Exception {
        String token = adminToken();
        String sku = "MIC-" + System.nanoTime();

        String responseBody = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(
                                sku,
                                "Microphone",
                                "Streaming mic",
                                "149.99",
                                "USD",
                                true,
                                List.of("Audio")
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(responseBody).get("id").asText();

        mockMvc.perform(patch("/products/{productId}", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateProductPayload(
                                null,
                                "Studio Microphone",
                                "Streaming mic with boom arm",
                                "199.99",
                                null,
                                false,
                                List.of("Audio", "Studio")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Studio Microphone"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.categories[1]").value("Studio"));
    }

    private String adminToken() {
        Instant now = Instant.now();
        SecretKey signingKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject("admin@eventdriven.local")
                .issuer(JWT_ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .claim("uid", UUID.randomUUID().toString())
                .claim("roles", List.of("ADMIN"))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private record CreateProductPayload(
            String sku,
            String name,
            String description,
            String price,
            String currency,
            Boolean active,
            List<String> categories
    ) {
    }

    private record UpdateProductPayload(
            String sku,
            String name,
            String description,
            String price,
            String currency,
            Boolean active,
            List<String> categories
    ) {
    }
}
