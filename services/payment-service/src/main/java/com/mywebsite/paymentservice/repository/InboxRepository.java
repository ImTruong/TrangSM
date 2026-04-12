package com.mywebsite.paymentservice.repository;

import com.mywebsite.paymentservice.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<Inbox, String> {
}
