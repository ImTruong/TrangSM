package com.mywebsite.bookingservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long tripId;
    private Long customerId;
    private Long driverId;
    private Long vehicleTypeId;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String checkoutUrl;
    private BigDecimal price;
}

