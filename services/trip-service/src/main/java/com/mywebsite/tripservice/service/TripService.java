package com.mywebsite.tripservice.service;

import com.mywebsite.tripservice.kafka.DriverAcceptedEvent;
import com.mywebsite.tripservice.kafka.TripRequestEvent;
import com.mywebsite.tripservice.model.Trip;

public interface TripService {
    Trip getTripById(Long id);
    void createTrip(TripRequestEvent event);
    void handlePaymentSuccess(Long tripId);
    void handleDriverAccepted(DriverAcceptedEvent event);
    void handleTripStarted(Long tripId);
    void handleTripCompleted(Long tripId);
    void handleTripCancelled(Long tripId, String reason);
}
