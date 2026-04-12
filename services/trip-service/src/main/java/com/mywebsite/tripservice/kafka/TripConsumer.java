package com.mywebsite.tripservice.kafka;

import com.mywebsite.tripservice.service.TripService;
import com.mywebsite.tripservice.service.InboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripConsumer {
    private final TripService tripService;
    private final InboxService inboxService;

    @Transactional
    @KafkaListener(topics = "trip-request-topic", groupId = "trip-service-group")
    public void consumeTripRequest(TripRequestEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip request event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received trip request for ID: {}", event.getTripId());
        tripService.createTrip(event);
    }

    @Transactional
    @KafkaListener(topics = "payment-success-topic", groupId = "trip-service-group")
    public void consumePaymentSuccess(TripStatusUpdateEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate payment success event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received payment success for trip: {}", event.getTripId());
        tripService.handlePaymentSuccess(event.getTripId());
    }

    @Transactional
    @KafkaListener(topics = "driver-accepted-topic", groupId = "trip-service-group")
    public void consumeDriverAccepted(DriverAcceptedEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate driver accepted event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received driver accepted for trip: {}", event.getTripId());
        tripService.handleDriverAccepted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-started-topic", groupId = "trip-service-group")
    public void consumeTripStarted(TripStatusUpdateEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip started event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received trip started for trip: {}", event.getTripId());
        tripService.handleTripStarted(event.getTripId());
    }

    @Transactional
    @KafkaListener(topics = "trip-completed-topic", groupId = "trip-service-group")
    public void consumeTripCompleted(TripStatusUpdateEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip completed event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received trip completed for trip: {}", event.getTripId());
        tripService.handleTripCompleted(event.getTripId());
    }

    @Transactional
    @KafkaListener(topics = "trip-cancelled-topic", groupId = "trip-service-group")
    public void consumeTripCancelled(TripStatusUpdateEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip cancelled event: {}, skipping", event.getEventId());
            return;
        }
        log.info("Received trip cancelled for trip: {}", event.getTripId());
        tripService.handleTripCancelled(event.getTripId(), event.getCancelReason());
    }
}
