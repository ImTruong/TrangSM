package com.mywebsite.bookingservice.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bookingId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tripId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long driverId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleTypeId;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String checkoutUrl;
    private BigDecimal price;
}

