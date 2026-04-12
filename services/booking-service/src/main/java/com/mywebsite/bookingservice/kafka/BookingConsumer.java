package com.mywebsite.bookingservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mywebsite.bookingservice.event.PaymentResponseEvent;
import com.mywebsite.bookingservice.event.TripSnapshotEvent;
import com.mywebsite.bookingservice.service.BookingService;
import com.mywebsite.bookingservice.service.InboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingConsumer {
    private final BookingService bookingService;
    private final InboxService inboxService;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "payment-response-topic", groupId = "booking-service-group")
    public void consumePaymentResponse(String payload) {
        PaymentResponseEvent event;
        try {
            event = objectMapper.readValue(payload, PaymentResponseEvent.class);
        } catch (Exception ex) {
            log.error("Invalid payment response payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate payment response event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handlePaymentResponse(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-payment-success-response-topic", groupId = "booking-service-group")
    public void consumeTripPaymentSuccess(String payload) {
        TripSnapshotEvent event;
        try {
            event = objectMapper.readValue(payload, TripSnapshotEvent.class);
        } catch (Exception ex) {
            log.error("Invalid trip payment success payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip payment success event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripPaymentSuccess(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-accepted-response-topic", groupId = "booking-service-group")
    public void consumeTripAccepted(String payload) {
        TripSnapshotEvent event;
        try {
            event = objectMapper.readValue(payload, TripSnapshotEvent.class);
        } catch (Exception ex) {
            log.error("Invalid trip accepted payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip accepted event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripAccepted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-started-response-topic", groupId = "booking-service-group")
    public void consumeTripStarted(String payload) {
        TripSnapshotEvent event;
        try {
            event = objectMapper.readValue(payload, TripSnapshotEvent.class);
        } catch (Exception ex) {
            log.error("Invalid trip started payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip started event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripStarted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-completed-response-topic", groupId = "booking-service-group")
    public void consumeTripCompleted(String payload) {
        TripSnapshotEvent event;
        try {
            event = objectMapper.readValue(payload, TripSnapshotEvent.class);
        } catch (Exception ex) {
            log.error("Invalid trip completed payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip completed event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripCompleted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-cancelled-response-topic", groupId = "booking-service-group")
    public void consumeTripCancelled(String payload) {
        TripSnapshotEvent event;
        try {
            event = objectMapper.readValue(payload, TripSnapshotEvent.class);
        } catch (Exception ex) {
            log.error("Invalid trip cancelled payload: {}", payload, ex);
            return;
        }
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip cancelled event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripCancelled(event);
    }
}
