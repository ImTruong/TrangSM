package com.mywebsite.bookingservice.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    private Long bookingId;
    private Long tripId;
    private String status;
    private String paymentStatus;
    private String checkoutUrl;
}

