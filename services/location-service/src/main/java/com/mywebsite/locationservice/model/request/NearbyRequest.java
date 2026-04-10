package com.mywebsite.locationservice.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbyRequest {
    Long requestId;
    Double radiusKm;
    Integer limit;
    Double lng;
    Double lat;
    Long vehicleTypeId;
}
