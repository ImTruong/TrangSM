package com.mywebsite.bookingservice.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
}

