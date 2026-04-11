package com.mywebsite.locationservice.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbyRequest {
    Long requestId;
    Double radiusKm;
    Integer limit;
    BigDecimal lng;
    BigDecimal lat;
    Long vehicleTypeId;
}
