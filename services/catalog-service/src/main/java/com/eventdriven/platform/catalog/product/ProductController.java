package com.eventdriven.platform.catalog.product;

import com.eventdriven.platform.catalog.product.dto.CreateProductRequest;
import com.eventdriven.platform.catalog.product.dto.PagedProductsResponse;
import com.eventdriven.platform.catalog.product.dto.ProductResponse;
import com.eventdriven.platform.catalog.product.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List products with optional active/search filters")
    public PagedProductsResponse listProducts(@RequestParam(required = false) Boolean active,
                                              @RequestParam(required = false) String search,
                                              @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return productService.listProducts(active, search, pageable);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details by id")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }

    @PostMapping
    @Operation(summary = "Create a product (admin only)")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "Update a product (admin only)")
    public ProductResponse updateProduct(@PathVariable UUID productId,
                                         @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(productId, request);
    }
}
