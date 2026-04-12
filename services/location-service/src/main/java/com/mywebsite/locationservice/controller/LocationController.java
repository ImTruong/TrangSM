package com.mywebsite.locationservice.controller;

import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.model.response.NearbyDriver;
import com.mywebsite.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(
        @RequestBody LocationRequest req,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Name") String phone,
        @RequestHeader("X-User-Roles") String roles
    ) {
        System.out.println("Received location update from userId=" + userId + ", phone=" + phone + ", roles=" + roles);
        if (req.getDriverId() == null) {
            req.setDriverId(userId);
        }
        locationService.updateLocation(req);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/nearby")
    public ResponseEntity<NearbyDriver> getClosestDriver(
        @RequestParam Long requestId,
        @RequestParam Double radiusKm,
        @RequestParam BigDecimal lng,
        @RequestParam BigDecimal lat,
        @RequestParam Long vehicleTypeId
        ) {
        NearbyDriver nearbyDriver = locationService.getClosestDriver(requestId, radiusKm, lng, lat, vehicleTypeId);
        if (nearbyDriver == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(nearbyDriver, HttpStatus.OK);
    }
}
