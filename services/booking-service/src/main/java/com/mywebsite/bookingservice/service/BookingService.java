package com.mywebsite.bookingservice.service;

import com.mywebsite.bookingservice.dto.request.*;
import com.mywebsite.bookingservice.dto.response.*;
import com.mywebsite.bookingservice.event.PaymentResponseEvent;
import com.mywebsite.bookingservice.event.TripSnapshotEvent;

public interface BookingService {
    EstimateResponse estimate(EstimateRequest request);
    CreateBookingResponse createBooking(CreateBookingRequest request, Long userId, String roles);
    BookingResponse getBooking(Long id);
    BookingResponse acceptBooking(Long bookingId, DriverActionRequest request, Long userId, String roles);
    BookingResponse rejectBooking(Long bookingId, DriverActionRequest request, Long userId, String roles);
    BookingResponse startBooking(Long bookingId, DriverActionRequest request, Long userId, String roles);
    BookingResponse completeBooking(Long bookingId, DriverActionRequest request, Long userId, String roles);
    BookingResponse cancelBooking(Long bookingId, CancelBookingRequest request, Long userId, String roles);

    void handlePaymentResponse(PaymentResponseEvent event);
    void handleTripPaymentSuccess(TripSnapshotEvent event);
    void handleTripAccepted(TripSnapshotEvent event);
    void handleTripStarted(TripSnapshotEvent event);
    void handleTripCompleted(TripSnapshotEvent event);
    void handleTripCancelled(TripSnapshotEvent event);
}

