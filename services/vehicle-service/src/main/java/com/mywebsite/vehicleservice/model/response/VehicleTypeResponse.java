package com.mywebsite.vehicleservice.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleTypeResponse {
    Long id;
    Integer numberOfSeat;
    String name;
    Double pricePerKm;
}
