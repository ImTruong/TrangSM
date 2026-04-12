package com.mywebsite.vehicleservice.service;

import com.mywebsite.vehicleservice.model.response.VehicleResponse;
import com.mywebsite.vehicleservice.model.response.VehicleTypeResponse;
import java.util.List;

public interface VehicleService {
    List<VehicleTypeResponse> getAllVehicleTypes();
    VehicleResponse getVehicleById(Long id);
    VehicleResponse getVehicleByOwnerId(Long ownerId);
}
