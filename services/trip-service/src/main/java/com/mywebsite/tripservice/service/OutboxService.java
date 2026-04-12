package com.mywebsite.tripservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mywebsite.tripservice.model.Outbox;
import com.mywebsite.tripservice.model.OutboxStatus;
import com.mywebsite.tripservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    public void saveEvent(String aggregateId, String aggregateType, String eventType, Object payload) {
        String eventId = java.util.UUID.randomUUID().toString();
        com.fasterxml.jackson.databind.node.ObjectNode node = objectMapper.valueToTree(payload);
        node.put("eventId", eventId);
        String jsonPayload = objectMapper.writeValueAsString(node);
        
        Outbox outbox = Outbox.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(jsonPayload)
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outbox);
    }
}
