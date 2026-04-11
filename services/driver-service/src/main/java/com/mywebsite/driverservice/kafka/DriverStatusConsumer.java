package com.mywebsite.driverservice.kafka;

import com.mywebsite.driverservice.model.event.DriverStatusChangeEvent;
import com.mywebsite.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverStatusConsumer {

    private final DriverService driverService;

    @KafkaListener(topics = "driver-status-topic", groupId = "driver-service-group")
    public void consumeStatusChange(DriverStatusChangeEvent event) {
        log.info("Received status change event for driver {}: {}", event.getDriverId(), event.getNewStatus());
        try {
            driverService.updateDriverStatus(event.getDriverId(), event.getNewStatus());
        } catch (Exception e) {
            log.error("Failed to update driver status: {}", e.getMessage());
        }
    }
}
