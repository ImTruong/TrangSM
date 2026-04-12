package com.mywebsite.bookingservice.controller;

import com.mywebsite.bookingservice.dto.request.*;
import com.mywebsite.bookingservice.dto.response.*;
import com.mywebsite.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/estimate")
    public ResponseEntity<EstimateResponse> estimate(
        @RequestParam Double pickUpLongitude,
        @RequestParam Double pickUpLatitude,
        @RequestParam Double dropOffLongitude,
        @RequestParam Double dropOffLatitude,
        @RequestParam Long vehicleTypeId
    ) {
        EstimateResponse response = bookingService.estimate(EstimateRequest.builder()
            .pickUpLongitude(pickUpLongitude)
            .pickUpLatitude(pickUpLatitude)
            .dropOffLongitude(dropOffLongitude)
            .dropOffLatitude(dropOffLatitude)
            .vehicleTypeId(vehicleTypeId)
            .build());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
        @RequestBody CreateBookingRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return new ResponseEntity<>(bookingService.createBooking(request, userId, roles), HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(
        @PathVariable Long id,
        @RequestBody DriverActionRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(bookingService.acceptBooking(id, request, userId, roles));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(
        @PathVariable Long id,
        @RequestBody DriverActionRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(bookingService.rejectBooking(id, request, userId, roles));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<BookingResponse> startBooking(
        @PathVariable Long id,
        @RequestBody DriverActionRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(bookingService.startBooking(id, request, userId, roles));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
        @PathVariable Long id,
        @RequestBody DriverActionRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(bookingService.completeBooking(id, request, userId, roles));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
        @PathVariable Long id,
        @RequestBody CancelBookingRequest request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, request, userId, roles));
    }
}
