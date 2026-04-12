package com.mywebsite.bookingservice.kafka;

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

    @Transactional
    @KafkaListener(topics = "payment-response-topic", groupId = "booking-service-group")
    public void consumePaymentResponse(PaymentResponseEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate payment response event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handlePaymentResponse(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-payment-success-response-topic", groupId = "booking-service-group")
    public void consumeTripPaymentSuccess(TripSnapshotEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip payment success event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripPaymentSuccess(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-accepted-response-topic", groupId = "booking-service-group")
    public void consumeTripAccepted(TripSnapshotEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip accepted event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripAccepted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-started-response-topic", groupId = "booking-service-group")
    public void consumeTripStarted(TripSnapshotEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip started event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripStarted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-completed-response-topic", groupId = "booking-service-group")
    public void consumeTripCompleted(TripSnapshotEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip completed event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripCompleted(event);
    }

    @Transactional
    @KafkaListener(topics = "trip-cancelled-response-topic", groupId = "booking-service-group")
    public void consumeTripCancelled(TripSnapshotEvent event) {
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Duplicate trip cancelled event: {}, skipping", event.getEventId());
            return;
        }
        bookingService.handleTripCancelled(event);
    }
}

