package com.mywebsite.bookingservice.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String name;
    private String phone;
    private String avatarUrl;
    private String licenseNumber;
    private String status;
}

