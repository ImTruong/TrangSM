package com.mywebsite.driverservice.service;

import com.mywebsite.driverservice.exception.DriverNotFoundException;
import com.mywebsite.driverservice.model.entity.Driver;
import com.mywebsite.driverservice.model.entity.DriverStatus;
import com.mywebsite.driverservice.model.response.DriverResponse;
import com.mywebsite.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        return mapToDriverResponse(driver);
    }

    @Override
    @Transactional
    public void updateDriverStatus(Long id, DriverStatus status) {
        log.info("Updating status for driver {} to {}", id, status);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        driver.setStatus(status);
        driverRepository.save(driver);
    }

    private DriverResponse mapToDriverResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .phone(driver.getPhone())
                .avatarUrl(driver.getAvatarUrl())
                .licenseNumber(driver.getLicenseNumber())
                .status(driver.getStatus())
                .build();
    }
}
