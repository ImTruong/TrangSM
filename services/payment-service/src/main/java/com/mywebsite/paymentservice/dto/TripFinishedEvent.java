package com.mywebsite.paymentservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripFinishedEvent {
    private String eventId;
    private String tripId;
    private String status; // COMPLETED
}
