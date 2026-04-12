package com.mywebsite.tripservice.repository;

import com.mywebsite.tripservice.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {
}
