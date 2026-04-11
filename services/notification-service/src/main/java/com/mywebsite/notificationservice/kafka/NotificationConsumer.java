package com.mywebsite.notificationservice.kafka;

import com.mywebsite.notificationservice.model.NotificationMessage;
import com.mywebsite.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-service-group")
    public void consumeNotification(NotificationMessage message) {
        log.info("Received notification via Kafka for user: {}", message.getRecipientId());
        notificationService.sendNotification(message);
    }
}
