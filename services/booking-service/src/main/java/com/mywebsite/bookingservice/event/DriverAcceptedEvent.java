package com.mywebsite.bookingservice.event;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAcceptedEvent {
    private Long tripId;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private String vehiclePlate;
    private String vehicleTypeName;
    private Integer vehicleSeat;
    private BigDecimal vehiclePrice;
    private String eventId;
}

