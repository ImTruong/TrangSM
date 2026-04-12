package com.mywebsite.bookingservice.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String plate;
    private VehicleTypeSnapshot vehicleType;
    private String color;
    private Long ownerId;
}

