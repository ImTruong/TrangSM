package com.mywebsite.notificationservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mywebsite.notificationservice.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserId(session);
        if (userId != null) {
            sessions.put(userId, session);
            log.info("WebSocket connection established for user: {}", userId);
        } else {
            log.warn("WebSocket connection attempt without userId");
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
                log.error("Error closing session: {}", e.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            log.info("WebSocket connection closed for user: {}", userId);
        }
    }

    public void sendNotification(NotificationMessage message) {
        WebSocketSession session = sessions.get(message.getRecipientId());
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
                log.info("Sent notification to user: {}", message.getRecipientId());
            } catch (IOException e) {
                log.error("Failed to send notification to user {}: {}", message.getRecipientId(), e.getMessage());
            }
        } else {
            log.warn("User {} not connected via WebSocket, notification queued or dismissed", message.getRecipientId());
        }
    }

    private String getUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            Map<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().toSingleValueMap();
            return queryParams.get("userId");
        }
        return null;
    }
}
