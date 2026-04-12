package com.mywebsite.tripservice.repository;

import com.mywebsite.tripservice.model.Outbox;
import com.mywebsite.tripservice.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByStatus(OutboxStatus status);
}
