package com.mywebsite.tripservice.kafka;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStatusUpdateEvent {
    private Long tripId;
    private String status;
    private String cancelReason;
    private String eventId;
}
