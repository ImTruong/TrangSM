package com.mywebsite.bookingservice.client;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String recipientId;
    private String title;
    private String content;
    private Map<String, Object> data;
    private String eventId;
}

