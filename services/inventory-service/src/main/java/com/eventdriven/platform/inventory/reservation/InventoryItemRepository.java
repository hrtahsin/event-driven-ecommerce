package com.eventdriven.platform.inventory.reservation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItemEntity> findByProductId(UUID productId);
}
