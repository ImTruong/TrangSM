package com.mywebsite.bookingservice.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeSnapshot {
    private Long id;
    private Integer numberOfSeat;
    private String name;
    private Double pricePerKm;
}

