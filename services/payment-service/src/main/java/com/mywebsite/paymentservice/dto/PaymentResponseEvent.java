package com.mywebsite.paymentservice.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseEvent {
    private String eventId;
    private String paymentId;
    private Long tripId;
    private Long customerId;
    private String status; // PAID, FAILED, PENDING
    private String method;
    private BigDecimal amount;
    private String stripeCheckoutUrl; // For frontend to redirect if online
}
