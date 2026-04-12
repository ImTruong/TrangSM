package com.mywebsite.tripservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    private Long id;

    private Long customerId;
    private String customerNameSnapshot;
    private String customerPhoneSnapshot;

    private Long driverId;
    private String driverNameSnapshot;
    private String driverPhoneSnapshot;

    private String vehiclePlateSnapshot;
    private String vehicleTypeNameSnapshot;
    private Integer vehicleSeatSnapshot;
    private BigDecimal vehiclePriceSnapshot;

    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private Double dropOffLongitude;
    private Double dropOffLatitude;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private BigDecimal price;
    private String cancelReason;

    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelAt;
}
