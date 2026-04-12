package com.mywebsite.paymentservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
    private String code;
    private String displayName;
}

