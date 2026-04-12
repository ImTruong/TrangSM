package com.mywebsite.bookingservice.client;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriverResponse {
    private Long driverId;
    private BigDecimal lng;
    private BigDecimal lat;
}

