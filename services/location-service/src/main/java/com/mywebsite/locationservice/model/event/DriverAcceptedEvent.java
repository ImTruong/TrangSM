package com.mywebsite.locationservice.model.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverAcceptedEvent {
    Long driverId;
    Long vehicleTypeId;
}
