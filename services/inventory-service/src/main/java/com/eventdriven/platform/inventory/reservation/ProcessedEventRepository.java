package com.eventdriven.platform.inventory.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long> {

    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
