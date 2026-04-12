package com.mywebsite.vehicleservice.controller;

import com.mywebsite.vehicleservice.model.response.VehicleResponse;
import com.mywebsite.vehicleservice.model.response.VehicleTypeResponse;
import com.mywebsite.vehicleservice.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/vehicles/types")
    public ResponseEntity<List<VehicleTypeResponse>> getAllVehicleTypes() {
        return ResponseEntity.ok(vehicleService.getAllVehicleTypes());
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }
}
