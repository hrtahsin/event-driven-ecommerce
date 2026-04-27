package com.eventdriven.platform.order.orders;

import com.eventdriven.platform.order.catalog.CatalogClient;
import com.eventdriven.platform.order.catalog.ProductSnapshot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceIntegrationTest {

    private static final String JWT_SECRET = "change-me-phase-2-shared-jwt-secret-1234567890";
    private static final String JWT_ISSUER = "identity-service";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("order_db")
            .withUsername("platform")
            .withPassword("platform");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CatalogClient catalogClient;

    @Test
    void shouldCreateReadAndListCustomerOrder() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String token = customerToken(customerId);
        when(catalogClient.getProduct(productId)).thenReturn(new ProductSnapshot(
                productId,
                "CAM-1001",
                "Camera Kit",
                new BigDecimal("799.99"),
                "USD",
                true
        ));

        String responseBody = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderPayload(
                                List.of(new CreateOrderItemPayload(
                                        productId,
                                        2
                                ))
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.totalAmount").value(1599.98))
                .andExpect(jsonPath("$.items[0].sku").value("CAM-1001"))
                .andExpect(jsonPath("$.items[0].lineTotal").value(1599.98))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode order = objectMapper.readTree(responseBody);
        String orderId = order.get("id").asText();

        mockMvc.perform(get("/orders/{orderId}", orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.totalAmount").value(1599.98));

        mockMvc.perform(get("/orders/mine")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].id").value(orderId));
    }

    @Test
    void shouldRejectInactiveCatalogProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        when(catalogClient.getProduct(productId)).thenReturn(new ProductSnapshot(
                productId,
                "ARCHIVED-1",
                "Archived Product",
                new BigDecimal("10.00"),
                "USD",
                false
        ));

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + customerToken(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderPayload(
                                List.of(new CreateOrderItemPayload(productId, 1))
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product is not active: " + productId));
    }

    @Test
    void shouldRejectInvalidOrderPayload() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + customerToken(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldReturnNotFoundForUnknownOrder() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + customerToken(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void shouldRejectOrderEndpointsWithoutToken() throws Exception {
        mockMvc.perform(get("/orders/mine"))
                .andExpect(status().isUnauthorized());
    }

    private String customerToken(UUID userId) {
        Instant now = Instant.now();
        SecretKey signingKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject("customer@example.com")
                .issuer(JWT_ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .claim("uid", userId.toString())
                .claim("roles", List.of("CUSTOMER"))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private record CreateOrderPayload(
            List<CreateOrderItemPayload> items
    ) {
    }

    private record CreateOrderItemPayload(
            UUID productId,
            Integer quantity
    ) {
    }
}
