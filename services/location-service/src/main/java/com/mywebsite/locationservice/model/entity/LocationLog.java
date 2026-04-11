package com.mywebsite.locationservice.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_log")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "driver_id")
    Integer driverId;

    @Column(precision = 10, scale = 8)
    BigDecimal lat;

    @Column(precision = 11, scale = 8)
    BigDecimal lng;

    @Column(name = "vehicle_type_id")
    Integer vehicleTypeId;

    @Column(name = "timestamp", insertable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime timestamp;
}
