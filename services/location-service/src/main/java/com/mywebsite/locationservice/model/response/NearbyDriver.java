package com.mywebsite.locationservice.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbyDriver {
    Long driverId;
    BigDecimal lng;
    BigDecimal lat;
}
