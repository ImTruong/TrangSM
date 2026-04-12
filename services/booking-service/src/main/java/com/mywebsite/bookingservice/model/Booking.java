package com.mywebsite.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private Long id;

    private Long tripId;
    private Long customerId;
    private Long driverId;
    private Long vehicleTypeId;

    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private Double dropOffLongitude;
    private Double dropOffLatitude;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentId;
    private String paymentStatus;
    private String paymentCheckoutUrl;

    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

