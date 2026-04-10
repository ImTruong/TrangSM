package com.mywebsite.locationservice.consumer;

import com.mywebsite.locationservice.model.event.DriverAcceptedEvent;
import com.mywebsite.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverEventConsumer {
    private final LocationService locationService;

    //    TODO: SỬA LẠI CHO KHỚP
    @KafkaListener(
        topics = "driver.accepted",
        groupId = "location-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDriverAccepted(DriverAcceptedEvent event) {
        try {
            if (event.getDriverId() != null && event.getVehicleTypeId() != null) {
                locationService.handleDriverAccepted(event);
            }
        } catch (Exception e) {
            log.error("Error processing DRIVER_ACCEPTED: {}", event, e);
            throw e; // để Kafka retry
        }
    }
}
