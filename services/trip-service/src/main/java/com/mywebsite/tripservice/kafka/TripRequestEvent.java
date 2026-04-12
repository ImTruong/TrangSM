package com.mywebsite.tripservice.kafka;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestEvent {
    private Long tripId;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private Double dropOffLongitude;
    private Double dropOffLatitude;
    private BigDecimal price;

    // Vehicle info requested by user
    private String vehiclePlate;
    private String vehicleTypeName;
    private Integer vehicleSeat;
    private BigDecimal vehiclePrice;

    private String eventId;
}
