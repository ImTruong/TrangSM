package com.mywebsite.vehicleservice.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleResponse {
    Long id;
    String plate;
    VehicleTypeResponse vehicleType;
    String color;
    Long ownerId;
}
