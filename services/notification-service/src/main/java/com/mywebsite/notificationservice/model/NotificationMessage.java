package com.mywebsite.notificationservice.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationMessage {
    String recipientId;
    String title;
    String content;
    Map<String, Object> data;
    String eventId;
}
