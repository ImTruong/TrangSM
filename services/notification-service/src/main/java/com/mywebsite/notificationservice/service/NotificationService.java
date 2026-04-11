package com.mywebsite.notificationservice.service;

import com.mywebsite.notificationservice.model.NotificationMessage;

public interface NotificationService {
    void sendNotification(NotificationMessage message);
}
