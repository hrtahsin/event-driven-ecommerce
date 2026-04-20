package com.eventdriven.platform.catalog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    @Query("select c from CategoryEntity c where lower(c.name) in :names")
    List<CategoryEntity> findAllByNormalizedNames(@Param("names") Set<String> names);
}
