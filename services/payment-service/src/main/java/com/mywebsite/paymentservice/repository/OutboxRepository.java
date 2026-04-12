package com.mywebsite.paymentservice.repository;

import com.mywebsite.paymentservice.model.Outbox;
import com.mywebsite.paymentservice.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByStatus(OutboxStatus status);
}
