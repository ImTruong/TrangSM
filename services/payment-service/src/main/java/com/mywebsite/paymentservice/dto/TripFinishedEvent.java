package com.mywebsite.paymentservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripFinishedEvent {
    private String eventId;
    private Long tripId;
    private String status; // COMPLETED
}
