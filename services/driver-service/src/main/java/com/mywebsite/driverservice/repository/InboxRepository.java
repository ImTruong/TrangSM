package com.mywebsite.driverservice.repository;

import com.mywebsite.driverservice.model.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {
}
