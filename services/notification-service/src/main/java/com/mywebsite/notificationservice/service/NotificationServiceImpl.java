package com.mywebsite.notificationservice.service;

import com.mywebsite.notificationservice.model.NotificationMessage;
import com.mywebsite.notificationservice.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationWebSocketHandler webSocketHandler;

    @Override
    public void sendNotification(NotificationMessage message) {
        log.info("Processing notification for recipient: {}", message.getRecipientId());
        webSocketHandler.sendNotification(message);
    }
}
