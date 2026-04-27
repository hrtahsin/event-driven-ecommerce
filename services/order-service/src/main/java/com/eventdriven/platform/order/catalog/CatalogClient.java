package com.eventdriven.platform.order.catalog;

import com.eventdriven.platform.order.support.ResourceNotFoundException;
import com.eventdriven.platform.order.support.UpstreamServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
public class CatalogClient {

    private final RestClient catalogRestClient;

    public CatalogClient(RestClient catalogRestClient) {
        this.catalogRestClient = catalogRestClient;
    }

    public ProductSnapshot getProduct(UUID productId) {
        try {
            CatalogProductResponse response = catalogRestClient.get()
                    .uri("/products/{productId}", productId)
                    .retrieve()
                    .body(CatalogProductResponse.class);

            if (response == null) {
                throw new UpstreamServiceException("Catalog returned an empty product response");
            }
            return response.toSnapshot();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Product not found: " + productId);
            }
            throw new UpstreamServiceException("Catalog product lookup failed", exception);
        } catch (RestClientException exception) {
            throw new UpstreamServiceException("Catalog service is unavailable", exception);
        }
    }
}
