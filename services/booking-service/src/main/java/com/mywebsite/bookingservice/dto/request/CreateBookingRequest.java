package com.mywebsite.bookingservice.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private Double dropOffLongitude;
    private Double dropOffLatitude;
    private Long vehicleTypeId;
    private String paymentMethod;
}

