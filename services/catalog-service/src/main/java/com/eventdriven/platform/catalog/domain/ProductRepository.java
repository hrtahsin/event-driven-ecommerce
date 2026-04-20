package com.eventdriven.platform.catalog.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    boolean existsBySkuIgnoreCase(String sku);

    Optional<ProductEntity> findBySkuIgnoreCase(String sku);

    @Override
    @EntityGraph(attributePaths = "categories")
    Optional<ProductEntity> findById(UUID id);
}
