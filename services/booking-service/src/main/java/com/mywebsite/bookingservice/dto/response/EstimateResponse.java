package com.mywebsite.bookingservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateResponse {
    private Long vehicleTypeId;
    private String vehicleTypeName;
    private Double distanceKm;
    private BigDecimal pricePerKm;
    private BigDecimal estimatedAmount;
}

