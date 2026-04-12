package com.mywebsite.tripservice.service;

import com.mywebsite.tripservice.model.Outbox;
import com.mywebsite.tripservice.model.OutboxStatus;
import com.mywebsite.tripservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class OutboxScheduler {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRateString = "${app.outbox.polling-rate}")
    public void processOutbox() {
        List<Outbox> pendingEvents = outboxRepository.findByStatus(OutboxStatus.PENDING);
        for (Outbox event : pendingEvents) {
            try {
                // Topic name can be derived from eventType or aggregateType
                // Here we assume eventType is the topic name for simplicity
                kafkaTemplate.send(event.getEventType(), event.getAggregateId(), event.getPayload());
                event.setStatus(OutboxStatus.PROCESSED);
                outboxRepository.save(event);
                log.info("Successfully sent event {} to Kafka", event.getId());
            } catch (Exception e) {
                log.error("Failed to send event {} to Kafka", event.getId(), e);
                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}
