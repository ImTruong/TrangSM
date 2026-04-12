package com.mywebsite.driverservice.kafka;

import com.mywebsite.driverservice.model.event.DriverStatusChangeEvent;
import com.mywebsite.driverservice.service.DriverService;
import com.mywebsite.driverservice.service.InboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverStatusConsumer {

    private final DriverService driverService;
    private final InboxService inboxService;

    @Transactional
    @KafkaListener(topics = "driver-status-topic", groupId = "driver-service-group")
    public void consumeStatusChange(DriverStatusChangeEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate driver status change event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received status change event for driver {}: {}", event.getDriverId(), event.getNewStatus());
        try {
            driverService.updateDriverStatus(event.getDriverId(), event.getNewStatus());
        } catch (Exception e) {
            log.error("Failed to update driver status: {}", e.getMessage());
        }
    }
}
