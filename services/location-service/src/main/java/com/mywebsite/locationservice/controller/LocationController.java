package com.mywebsite.locationservice.controller;

import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.model.request.NearbyRequest;
import com.mywebsite.locationservice.model.response.NearbyDriver;
import com.mywebsite.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(
        @RequestBody LocationRequest req,
        @RequestHeader("X-User-Id") String userId,
        @RequestHeader("X-User-Name") String phone,
        @RequestHeader("X-User-Roles") String roles
    ) {
        System.out.println("Received location update from userId=" + userId + ", phone=" + phone + ", roles=" + roles);
        locationService.updateLocation(req);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/nearby")
    public ResponseEntity<NearbyDriver> getClosestDriver(
        @RequestBody NearbyRequest req
    ) {
        NearbyDriver nearbyDriver = locationService.getClosestDriver(req);
        if (nearbyDriver == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(nearbyDriver, HttpStatus.OK);
    }
}
