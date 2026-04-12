package com.mywebsite.bookingservice.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeResponse {
    private Long id;
    private Integer numberOfSeat;
    private String name;
    private Double pricePerKm;
}

