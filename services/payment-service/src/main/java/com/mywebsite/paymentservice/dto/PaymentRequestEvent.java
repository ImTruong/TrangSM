package com.mywebsite.paymentservice.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestEvent {
    private String eventId;
    private String customerId;
    private String tripId;
    private BigDecimal amount;
    private String currency;
    private String method; // ONLINE or CASH
}
