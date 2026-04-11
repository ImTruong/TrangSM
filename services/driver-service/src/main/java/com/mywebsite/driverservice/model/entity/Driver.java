package com.mywebsite.driverservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String phone;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "license_number")
    String licenseNumber;

    @Enumerated(EnumType.STRING)
    DriverStatus status;
}
