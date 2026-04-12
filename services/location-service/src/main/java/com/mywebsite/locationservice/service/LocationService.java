package com.mywebsite.locationservice.service;

import com.mywebsite.locationservice.model.event.DriverAcceptedEvent;
import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.model.response.NearbyDriver;

import java.math.BigDecimal;

public interface LocationService {
    void updateLocation(LocationRequest req);

    void handleDriverAccepted(DriverAcceptedEvent event);

    NearbyDriver getClosestDriver(Long requestId, Double radiusKm, BigDecimal lng, BigDecimal lat, Long vehicleTypeID);
}
