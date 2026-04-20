package com.eventdriven.platform.catalog.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class CatalogServiceInfoController {

    @GetMapping
    public CatalogServiceInfoResponse describe() {
        return new CatalogServiceInfoResponse(
                "catalog-service",
                "Product catalog and pricing metadata",
                List.of("products", "categories", "product_categories"),
                List.of("GET /products", "GET /products/{id}", "POST /products", "PATCH /products/{id}"),
                List.of(),
                List.of()
        );
    }
}
