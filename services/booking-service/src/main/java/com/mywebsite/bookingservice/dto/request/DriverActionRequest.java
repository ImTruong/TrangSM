package com.mywebsite.bookingservice.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverActionRequest {
    private Long driverId;
}

