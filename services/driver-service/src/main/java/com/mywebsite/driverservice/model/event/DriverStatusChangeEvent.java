package com.mywebsite.driverservice.model.event;

import com.mywebsite.driverservice.model.entity.DriverStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverStatusChangeEvent {
    Long driverId;
    DriverStatus newStatus;
    String eventId;
}
