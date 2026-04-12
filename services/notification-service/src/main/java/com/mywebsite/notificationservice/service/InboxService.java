package com.mywebsite.notificationservice.service;

import com.mywebsite.notificationservice.model.Inbox;
import com.mywebsite.notificationservice.repository.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InboxService {
    private final InboxRepository inboxRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isProcessed(String messageId) {
        if (messageId == null) return false;
        
        if (inboxRepository.existsById(messageId)) {
            return true;
        }
        
        Inbox inbox = Inbox.builder()
                .messageId(messageId)
                .processedAt(LocalDateTime.now())
                .build();
        inboxRepository.save(inbox);
        return false;
    }
}
