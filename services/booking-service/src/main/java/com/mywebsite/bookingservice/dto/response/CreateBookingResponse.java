package com.mywebsite.bookingservice.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bookingId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tripId;
    private String status;
    private String paymentStatus;
    private String checkoutUrl;
}

