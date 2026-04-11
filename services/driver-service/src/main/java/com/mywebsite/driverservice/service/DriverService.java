package com.mywebsite.driverservice.service;

import com.mywebsite.driverservice.model.entity.DriverStatus;
import com.mywebsite.driverservice.model.response.DriverResponse;

public interface DriverService {
    DriverResponse getDriverById(Long id);
    void updateDriverStatus(Long id, DriverStatus status);
}
