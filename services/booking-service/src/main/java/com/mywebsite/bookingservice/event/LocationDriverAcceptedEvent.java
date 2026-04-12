package com.mywebsite.bookingservice.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDriverAcceptedEvent {
    private Long driverId;
    private Long vehicleTypeId;
    private String eventId;
}

