package com.mywebsite.paymentservice.service;

import com.mywebsite.paymentservice.dto.PaymentRequestEvent;

public interface PaymentService {
    void processPaymentRequest(PaymentRequestEvent event);
    void handleStripeSuccess(String sessionId, String paymentIntentId, String rawData);
    void handleStripeFailure(String sessionId, String rawData);
    void finishCashTrip(Long tripId);
    void publishPaymentFailure(PaymentRequestEvent event, String reason);
}
