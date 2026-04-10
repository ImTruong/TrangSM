package com.mywebsite.locationservice.service;

import com.mywebsite.locationservice.model.event.DriverAcceptedEvent;
import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.model.request.NearbyRequest;
import com.mywebsite.locationservice.model.response.NearbyDriver;

public interface LocationService {
    void updateLocation(LocationRequest req);

    NearbyDriver getClosestDriver(NearbyRequest req);

    void handleDriverAccepted(DriverAcceptedEvent event);
}
