package com.mywebsite.tripservice.service;

import com.mywebsite.tripservice.kafka.DriverAcceptedEvent;
import com.mywebsite.tripservice.kafka.TripRequestEvent;
import com.mywebsite.tripservice.model.Trip;
import com.mywebsite.tripservice.model.TripStatus;
import com.mywebsite.tripservice.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final OutboxService outboxService;
    private final InboxService inboxService;

    @Override
    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    @Override
    @Transactional
    public void createTrip(TripRequestEvent event) {
        Trip trip = Trip.builder()
                .id(event.getTripId())
                .customerId(event.getCustomerId())
                .customerNameSnapshot(event.getCustomerName())
                .customerPhoneSnapshot(event.getCustomerPhone())
                .pickUpLongitude(event.getPickUpLongitude())
                .pickUpLatitude(event.getPickUpLatitude())
                .dropOffLongitude(event.getDropOffLongitude())
                .dropOffLatitude(event.getDropOffLatitude())
                .price(event.getPrice())
                .vehiclePlateSnapshot(event.getVehiclePlate())
                .vehicleTypeNameSnapshot(event.getVehicleTypeName())
                .vehicleSeatSnapshot(event.getVehicleSeat())
                .vehiclePriceSnapshot(event.getVehiclePrice())
                .status(TripStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        tripRepository.save(trip);
        
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-created-topic", trip);
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.FINDING_DRIVER);
        log.info("Payment success for trip: {}, status updated to FINDING_DRIVER", tripId);
        
        tripRepository.save(trip);
        
        // Publish success event back to Kafka
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-payment-success-response-topic", trip);
    }

    @Override
    @Transactional
    public void handleDriverAccepted(DriverAcceptedEvent event) {
        Trip trip = getTripById(event.getTripId());
        trip.setDriverId(event.getDriverId());
        trip.setDriverNameSnapshot(event.getDriverName());
        trip.setDriverPhoneSnapshot(event.getDriverPhone());
        trip.setVehiclePlateSnapshot(event.getVehiclePlate());
        trip.setVehicleTypeNameSnapshot(event.getVehicleTypeName());
        trip.setVehicleSeatSnapshot(event.getVehicleSeat());
        trip.setVehiclePriceSnapshot(event.getVehiclePrice());
        trip.setStatus(TripStatus.ACCEPTED);
        trip.setAcceptedAt(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-accepted-response-topic", trip);
    }

    @Override
    @Transactional
    public void handleTripStarted(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.STARTED);
        trip.setStartedAt(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-started-response-topic", trip);
    }

    @Override
    @Transactional
    public void handleTripCompleted(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-completed-response-topic", trip);
    }

    @Override
    @Transactional
    public void handleTripCancelled(Long tripId, String reason) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.CANCELLED);
        trip.setCancelReason(reason);
        trip.setCancelAt(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        outboxService.saveEvent(trip.getId().toString(), "TRIP", "trip-cancelled-response-topic", trip);
    }
}
