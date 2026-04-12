package com.mywebsite.bookingservice.repository;

import com.mywebsite.bookingservice.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<Inbox, String> {
}

