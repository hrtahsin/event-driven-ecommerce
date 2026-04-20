package com.eventdriven.platform.catalog.product;

import com.eventdriven.platform.catalog.domain.CategoryEntity;
import com.eventdriven.platform.catalog.domain.CategoryRepository;
import com.eventdriven.platform.catalog.domain.ProductEntity;
import com.eventdriven.platform.catalog.domain.ProductRepository;
import com.eventdriven.platform.catalog.product.dto.CreateProductRequest;
import com.eventdriven.platform.catalog.product.dto.PagedProductsResponse;
import com.eventdriven.platform.catalog.product.dto.ProductResponse;
import com.eventdriven.platform.catalog.product.dto.ProductSummaryResponse;
import com.eventdriven.platform.catalog.product.dto.UpdateProductRequest;
import com.eventdriven.platform.catalog.support.ResourceConflictException;
import com.eventdriven.platform.catalog.support.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public PagedProductsResponse listProducts(Boolean active, String search, Pageable pageable) {
        Page<ProductEntity> page = productRepository.findAll(ProductSpecifications.filter(active, search), pageable);
        return new PagedProductsResponse(
                page.getContent().stream().map(this::toSummary).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        return toResponse(findProduct(productId));
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String normalizedSku = normalizeSku(request.sku());
        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new ResourceConflictException("A product with that SKU already exists");
        }

        ProductEntity product = new ProductEntity();
        product.setSku(normalizedSku);
        product.setName(normalizeName(request.name()));
        product.setDescription(normalizeDescription(request.description()));
        product.setPrice(request.price());
        product.setCurrency(normalizeCurrency(request.currency()));
        product.setActive(request.active() == null || request.active());
        product.setCategories(resolveCategories(request.categories()));

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        ProductEntity product = findProduct(productId);

        if (request.sku() != null) {
            String normalizedSku = normalizeSku(request.sku());
            productRepository.findBySkuIgnoreCase(normalizedSku)
                    .filter(existing -> !existing.getId().equals(productId))
                    .ifPresent(existing -> {
                        throw new ResourceConflictException("A product with that SKU already exists");
                    });
            product.setSku(normalizedSku);
        }

        if (request.name() != null) {
            product.setName(normalizeName(request.name()));
        }
        if (request.description() != null) {
            product.setDescription(normalizeDescription(request.description()));
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.currency() != null) {
            product.setCurrency(normalizeCurrency(request.currency()));
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }
        if (request.categories() != null) {
            product.setCategories(resolveCategories(request.categories()));
        }

        return toResponse(productRepository.save(product));
    }

    private ProductEntity findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Set<CategoryEntity> resolveCategories(Set<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<String> normalizedNames = categories.stream()
                .map(this::normalizeCategoryName)
                .filter(name -> !name.isBlank())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        if (normalizedNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<String> loweredNames = normalizedNames.stream()
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        Map<String, CategoryEntity> existingByNormalizedName = new LinkedHashMap<>();
        categoryRepository.findAllByNormalizedNames(loweredNames).forEach(category ->
                existingByNormalizedName.put(category.getName().toLowerCase(), category)
        );

        Set<CategoryEntity> resolved = new LinkedHashSet<>();
        for (String categoryName : normalizedNames) {
            String key = categoryName.toLowerCase();
            CategoryEntity category = existingByNormalizedName.get(key);
            if (category == null) {
                CategoryEntity newCategory = new CategoryEntity();
                newCategory.setName(categoryName);
                category = categoryRepository.save(newCategory);
                existingByNormalizedName.put(key, category);
            }
            resolved.add(category);
        }

        return resolved;
    }

    private ProductSummaryResponse toSummary(ProductEntity product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getCurrency(),
                product.isActive(),
                categoryNames(product)
        );
    }

    private ProductResponse toResponse(ProductEntity product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.isActive(),
                categoryNames(product),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private java.util.List<String> categoryNames(ProductEntity product) {
        return product.getCategories().stream()
                .map(CategoryEntity::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private String normalizeDescription(String description) {
        return description == null ? null : description.trim();
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase();
    }

    private String normalizeCategoryName(String categoryName) {
        return categoryName == null ? "" : categoryName.trim();
    }
}
