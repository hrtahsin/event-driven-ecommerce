package com.eventdriven.platform.order.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findByAggregateIdAndEventType(String aggregateId, String eventType);

    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status, Pageable pageable);
}
