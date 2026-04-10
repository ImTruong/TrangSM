package com.mywebsite.locationservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_log")
@Getter
@Setter
public class LocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id")
    private Integer driverId;

    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    @Column(name = "vehicle_type_id")
    private Integer vehicleTypeId;

    @Column(name = "timestamp", insertable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
}
