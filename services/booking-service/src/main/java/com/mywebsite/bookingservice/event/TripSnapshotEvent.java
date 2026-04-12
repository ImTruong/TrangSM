package com.mywebsite.bookingservice.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSnapshotEvent {
    private Long id;
    private Long customerId;
    private Long driverId;
    private String status;
    private String eventId;
}

