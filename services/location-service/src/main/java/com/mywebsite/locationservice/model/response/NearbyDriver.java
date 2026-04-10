package com.mywebsite.locationservice.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbyDriver {
    Long driverId;
    Double lng;
    Double lat;
}
