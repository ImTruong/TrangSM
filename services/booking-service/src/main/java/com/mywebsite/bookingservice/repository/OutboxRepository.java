package com.mywebsite.bookingservice.repository;

import com.mywebsite.bookingservice.model.Outbox;
import com.mywebsite.bookingservice.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByStatus(OutboxStatus status);
}

