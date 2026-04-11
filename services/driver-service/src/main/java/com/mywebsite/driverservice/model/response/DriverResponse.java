package com.mywebsite.driverservice.model.response;

import com.mywebsite.driverservice.model.entity.DriverStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    Long id;
    String name;
    String phone;
    String avatarUrl;
    String licenseNumber;
    DriverStatus status;
}
