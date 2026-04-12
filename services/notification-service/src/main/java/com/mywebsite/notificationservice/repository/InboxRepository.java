package com.mywebsite.notificationservice.repository;

import com.mywebsite.notificationservice.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {
}
