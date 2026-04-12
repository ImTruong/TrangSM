package com.mywebsite.notificationservice.kafka;

import com.mywebsite.notificationservice.model.NotificationMessage;
import com.mywebsite.notificationservice.service.NotificationService;
import com.mywebsite.notificationservice.service.InboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final InboxService inboxService;

    @Transactional
    @KafkaListener(topics = "notification-topic", groupId = "notification-service-group")
    public void consumeNotification(NotificationMessage message) {
        if (inboxService.isProcessed(message.getEventId())) {
            log.info("Duplicate notification event: {}, skipping", message.getEventId());
            return;
        }
        log.info("Received notification via Kafka for user: {}", message.getRecipientId());
        notificationService.sendNotification(message);
    }
}
