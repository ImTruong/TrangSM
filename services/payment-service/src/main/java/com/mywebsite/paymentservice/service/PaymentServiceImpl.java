package com.mywebsite.paymentservice.service;

import com.mywebsite.paymentservice.dto.PaymentRequestEvent;
import com.mywebsite.paymentservice.dto.PaymentResponseEvent;
import com.mywebsite.paymentservice.model.Payment;
import com.mywebsite.paymentservice.model.PaymentMethod;
import com.mywebsite.paymentservice.model.PaymentStatus;
import com.mywebsite.paymentservice.repository.PaymentRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Override
    @Transactional
    public void processPaymentRequest(PaymentRequestEvent event) {
        log.info("Processing payment request for trip: {}", event.getTripId());

        PaymentMethod method = PaymentMethod.valueOf(event.getMethod().toUpperCase());
        
        Payment payment = Payment.builder()
                .customerId(event.getCustomerId())
                .tripId(event.getTripId())
                .method(method)
                .status(PaymentStatus.PENDING)
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        if (method == PaymentMethod.ONLINE) {
            createStripeSession(payment);
        } else {
            publishPaymentResponse(payment, null);
        }
    }

    @SneakyThrows
    private void createStripeSession(Payment payment) {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(payment.getCurrency())
                                .setUnitAmount(payment.getAmount().multiply(new BigDecimal(100)).longValue())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Trip Payment - " + payment.getTripId())
                                        .build())
                                .build())
                        .build())
                .setClientReferenceId(payment.getId().toString())
                .build();

        Session session = Session.create(params);
        payment.setStripeSessionId(session.getId());
        paymentRepository.save(payment);

        publishPaymentResponse(payment, session.getUrl());
    }

    @Override
    @Transactional
    public void handleStripeSuccess(String sessionId, String paymentIntentId, String rawData) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripeSessionId(sessionId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.PAID);
                payment.setStripePaymentIntentId(paymentIntentId);
                payment.setGatewayResponseData(rawData);
                paymentRepository.save(payment);

                publishPaymentResponse(payment, null);
                log.info("Payment SUCCESS updated for trip: {}", payment.getTripId());
            }
        }
    }

    @Override
    @Transactional
    public void handleStripeFailure(String sessionId, String rawData) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripeSessionId(sessionId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setGatewayResponseData(rawData);
                paymentRepository.save(payment);

                publishPaymentResponse(payment, null);
                log.info("Payment FAILED updated for trip: {}", payment.getTripId());
            }
        }
    }

    @Override
    @Transactional
    public void finishCashTrip(Long tripId) {
        Optional<Payment> paymentOpt = paymentRepository.findByTripId(tripId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (payment.getMethod() == PaymentMethod.CASH) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
                log.info("Cash payment COMPLETED for trip: {}", tripId);
            }
        }
    }

    @Override
    @Transactional
    public void publishPaymentFailure(PaymentRequestEvent event, String reason) {
        PaymentResponseEvent response = PaymentResponseEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .tripId(event.getTripId())
                .customerId(event.getCustomerId())
                .status(PaymentStatus.FAILED.name())
                .method(event.getMethod())
                .amount(event.getAmount())
                .build();

        outboxService.saveEvent(
                event.getTripId().toString(),
                "Payment",
                "payment-response-topic",
                response
        );
        log.error("Published payment failure for trip: {}. Reason: {}", event.getTripId(), reason);
    }

    private void publishPaymentResponse(Payment payment, String checkoutUrl) {
        PaymentResponseEvent response = PaymentResponseEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .paymentId(payment.getId().toString())
                .tripId(payment.getTripId())
                .customerId(payment.getCustomerId())
                .status(payment.getStatus().name())
                .method(payment.getMethod().name())
                .amount(payment.getAmount())
                .stripeCheckoutUrl(checkoutUrl)
                .build();

        outboxService.saveEvent(
                payment.getId().toString(),
                "Payment",
                "payment-response-topic",
                response
        );
    }
}
