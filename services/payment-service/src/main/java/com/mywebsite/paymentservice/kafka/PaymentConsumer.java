package com.mywebsite.paymentservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mywebsite.paymentservice.dto.PaymentRequestEvent;
import com.mywebsite.paymentservice.dto.TripFinishedEvent;
import com.mywebsite.paymentservice.service.InboxService;
import com.mywebsite.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentService paymentService;
    private final InboxService inboxService;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "create-payment-topic", groupId = "payment-service-group")
    public void consumePaymentRequest(PaymentRequestEvent event) {
        log.info("Received payment request event: {}", event.getEventId());
        
        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return;
        }

        try {
            paymentService.processPaymentRequest(event);
        } catch (Exception e) {
            log.error("Error processing payment request: {}", event.getEventId(), e);
            paymentService.publishPaymentFailure(event, e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "trip-completed-topic", groupId = "payment-service-group")
    public void consumeTripFinished(TripFinishedEvent event) {
        log.info("Received trip finished event: {}", event.getEventId());

        if (inboxService.isProcessed(event.getEventId())) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return;
        }

        try {
            paymentService.finishCashTrip(event.getTripId());
        } catch (Exception e) {
            log.error("Error processing trip finished: {}", event.getEventId(), e);
        }
    }
}
