package com.mywebsite.vehicleservice.service;

import com.mywebsite.vehicleservice.exception.VehicleNotFoundException;
import com.mywebsite.vehicleservice.model.entity.Vehicle;
import com.mywebsite.vehicleservice.model.entity.VehicleType;
import com.mywebsite.vehicleservice.model.response.VehicleResponse;
import com.mywebsite.vehicleservice.model.response.VehicleTypeResponse;
import com.mywebsite.vehicleservice.repository.VehicleRepository;
import com.mywebsite.vehicleservice.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public List<VehicleTypeResponse> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream()
                .map(this::mapToVehicleTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        return mapToVehicleResponse(vehicle);
    }

    private VehicleTypeResponse mapToVehicleTypeResponse(VehicleType type) {
        return VehicleTypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .numberOfSeat(type.getNumberOfSeat())
                .pricePerKm(type.getPricePerKm())
                .build();
    }

    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate())
                .color(vehicle.getColor())
                .ownerId(vehicle.getOwnerId())
                .vehicleType(mapToVehicleTypeResponse(vehicle.getVehicleType()))
                .build();
    }
}
