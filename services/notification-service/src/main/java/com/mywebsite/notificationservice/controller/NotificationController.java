package com.mywebsite.notificationservice.controller;

import com.mywebsite.notificationservice.model.NotificationMessage;
import com.mywebsite.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> postNotification(@RequestBody NotificationMessage message) {
        notificationService.sendNotification(message);
        return ResponseEntity.ok("Notification processed");
    }
}
