package com.mywebsite.bookingservice.service;

import com.mywebsite.bookingservice.client.*;
import com.mywebsite.bookingservice.dto.request.*;
import com.mywebsite.bookingservice.dto.response.*;
import com.mywebsite.bookingservice.event.*;
import com.mywebsite.bookingservice.model.*;
import com.mywebsite.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final OutboxService outboxService;
    private final RestTemplate restTemplate;

    @Value("${service.user.url}")
    private String userServiceUrl;
    @Value("${service.vehicle.url}")
    private String vehicleServiceUrl;
    @Value("${service.driver.url}")
    private String driverServiceUrl;
    @Value("${service.location.url}")
    private String locationServiceUrl;
    @Value("${service.notification.url}")
    private String notificationServiceUrl;

    @Override
    public EstimateResponse estimate(EstimateRequest request) {
        validateCoordinate(request.getPickUpLongitude(), request.getPickUpLatitude());
        validateCoordinate(request.getDropOffLongitude(), request.getDropOffLatitude());
        VehicleTypeResponse vehicleType = getVehicleType(request.getVehicleTypeId());

        double distanceKm = haversine(
            request.getPickUpLatitude(), request.getPickUpLongitude(),
            request.getDropOffLatitude(), request.getDropOffLongitude()
        );
        BigDecimal pricePerKm = BigDecimal.valueOf(vehicleType.getPricePerKm());
        BigDecimal estimated = pricePerKm.multiply(BigDecimal.valueOf(distanceKm)).setScale(2, RoundingMode.HALF_UP);

        return EstimateResponse.builder()
            .vehicleTypeId(vehicleType.getId())
            .vehicleTypeName(vehicleType.getName())
            .distanceKm(distanceKm)
            .pricePerKm(pricePerKm)
            .estimatedAmount(estimated)
            .build();
    }

    @Override
    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest request, Long userId, String roles) {
        requireRole(roles, "CUSTOMER");
        if (userId == null) {
            throw new IllegalArgumentException("Missing user id");
        }

        EstimateResponse estimate = estimate(EstimateRequest.builder()
            .pickUpLongitude(request.getPickUpLongitude())
            .pickUpLatitude(request.getPickUpLatitude())
            .dropOffLongitude(request.getDropOffLongitude())
            .dropOffLatitude(request.getDropOffLatitude())
            .vehicleTypeId(request.getVehicleTypeId())
            .build());

        UserResponse user = getUser(userId);
        VehicleTypeResponse vehicleType = getVehicleType(request.getVehicleTypeId());

        PaymentMethod method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        Long tripId = randomPositiveLong();

        Booking booking = Booking.builder()
            .id(tripId)
            .tripId(tripId)
            .customerId(userId)
            .vehicleTypeId(vehicleType.getId())
            .pickUpLongitude(request.getPickUpLongitude())
            .pickUpLatitude(request.getPickUpLatitude())
            .dropOffLongitude(request.getDropOffLongitude())
            .dropOffLatitude(request.getDropOffLatitude())
            .paymentMethod(method)
            .paymentStatus("PENDING")
            .status(method == PaymentMethod.CASH ? BookingStatus.FINDING_DRIVER : BookingStatus.PENDING_PAYMENT)
            .price(estimate.getEstimatedAmount())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        bookingRepository.save(booking);

        TripRequestEvent tripEvent = TripRequestEvent.builder()
            .tripId(tripId)
            .customerId(userId)
            .customerName(user.getFullName())
            .customerPhone(user.getPhoneNumber())
            .pickUpLongitude(request.getPickUpLongitude())
            .pickUpLatitude(request.getPickUpLatitude())
            .dropOffLongitude(request.getDropOffLongitude())
            .dropOffLatitude(request.getDropOffLatitude())
            .price(estimate.getEstimatedAmount())
            .vehiclePlate("N/A")
            .vehicleTypeName(vehicleType.getName())
            .vehicleSeat(vehicleType.getNumberOfSeat())
            .vehiclePrice(BigDecimal.valueOf(vehicleType.getPricePerKm()))
            .build();
        outboxService.saveEvent(tripId.toString(), "BOOKING", "trip-request-topic", tripEvent);

        PaymentRequestEvent paymentEvent = PaymentRequestEvent.builder()
            .customerId(userId)
            .tripId(tripId)
            .amount(estimate.getEstimatedAmount())
            .currency("USD")
            .method(method.name())
            .build();
        outboxService.saveEvent(tripId.toString(), "BOOKING", "create-payment-topic", paymentEvent);

        if (method == PaymentMethod.CASH) {
            publishPaymentSuccessEvent(tripId, "CASH");
        }

        return CreateBookingResponse.builder()
            .bookingId(booking.getId())
            .tripId(booking.getTripId())
            .status(booking.getStatus().name())
            .paymentStatus(booking.getPaymentStatus())
            .build();
    }

    @Override
    @Transactional
    public BookingResponse acceptBooking(Long bookingId, DriverActionRequest request, Long userId, String roles) {
        requireRole(roles, "DRIVER");
        Booking booking = findBookingById(bookingId);
        DriverResponse driver = getDriver(request.getDriverId());

        booking.setDriverId(driver.getId());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        DriverAcceptedEvent acceptedEvent = DriverAcceptedEvent.builder()
            .tripId(booking.getTripId())
            .driverId(driver.getId())
            .driverName(driver.getName())
            .driverPhone(driver.getPhone())
            .vehiclePlate("N/A")
            .vehicleTypeName("N/A")
            .vehicleSeat(0)
            .vehiclePrice(booking.getPrice())
            .build();
        outboxService.saveEvent(booking.getId().toString(), "BOOKING", "driver-accepted-topic", acceptedEvent);

        LocationDriverAcceptedEvent locationEvent = LocationDriverAcceptedEvent.builder()
            .driverId(driver.getId())
            .vehicleTypeId(booking.getVehicleTypeId())
            .build();
        outboxService.saveEvent(booking.getId().toString(), "BOOKING", "driver.accepted", locationEvent);

        sendNotification(
            booking.getCustomerId().toString(),
            "Da co tai xe",
            "Tai xe " + driver.getName() + " da nhan cuoc",
            Map.of("tripId", booking.getTripId(), "driver", driver, "paymentStatus", booking.getPaymentStatus())
        );

        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse rejectBooking(Long bookingId, DriverActionRequest request, Long userId, String roles) {
        requireRole(roles, "DRIVER");
        Booking booking = findBookingById(bookingId);
        findDriverAndNotify(booking);
        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse startBooking(Long bookingId, DriverActionRequest request, Long userId, String roles) {
        requireRole(roles, "DRIVER");
        Booking booking = findBookingById(bookingId);
        TripStatusUpdateEvent event = TripStatusUpdateEvent.builder()
            .tripId(booking.getTripId())
            .status("STARTED")
            .build();
        outboxService.saveEvent(booking.getId().toString(), "BOOKING", "trip-started-topic", event);
        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(Long bookingId, DriverActionRequest request, Long userId, String roles) {
        requireRole(roles, "DRIVER");
        Booking booking = findBookingById(bookingId);
        TripStatusUpdateEvent event = TripStatusUpdateEvent.builder()
            .tripId(booking.getTripId())
            .status("COMPLETED")
            .build();
        outboxService.saveEvent(booking.getId().toString(), "BOOKING", "trip-completed-topic", event);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, CancelBookingRequest request, Long userId, String roles) {
        Booking booking = findBookingById(bookingId);
        if (!Objects.equals(booking.getCustomerId(), userId)) {
            throw new IllegalArgumentException("Only booking owner can cancel");
        }
        if (booking.getStatus() == BookingStatus.ACCEPTED || booking.getStatus() == BookingStatus.STARTED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel after driver accepted");
        }

        TripStatusUpdateEvent event = TripStatusUpdateEvent.builder()
            .tripId(booking.getTripId())
            .status("CANCELLED")
            .cancelReason(request.getCancelReason())
            .build();
        outboxService.saveEvent(booking.getId().toString(), "BOOKING", "trip-cancelled-topic", event);

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Override
    public BookingResponse getBooking(Long id) {
        return toResponse(findBookingById(id));
    }

    @Override
    @Transactional
    public void handlePaymentResponse(PaymentResponseEvent event) {
        Booking booking = findBookingById(Long.valueOf(event.getTripId()));
        booking.setPaymentId(event.getPaymentId());
        booking.setPaymentStatus(event.getStatus());
        booking.setPaymentCheckoutUrl(event.getStripeCheckoutUrl());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        if ("PAID".equalsIgnoreCase(event.getStatus())) {
            publishPaymentSuccessEvent(booking.getTripId(), event.getMethod());
        }
    }

    @Override
    @Transactional
    public void handleTripPaymentSuccess(TripSnapshotEvent event) {
        Booking booking = findBookingById(event.getId());
        booking.setStatus(BookingStatus.FINDING_DRIVER);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        findDriverAndNotify(booking);
    }

    @Override
    @Transactional
    public void handleTripAccepted(TripSnapshotEvent event) {
        Booking booking = findBookingById(event.getId());
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void handleTripStarted(TripSnapshotEvent event) {
        Booking booking = findBookingById(event.getId());
        booking.setStatus(BookingStatus.STARTED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void handleTripCompleted(TripSnapshotEvent event) {
        Booking booking = findBookingById(event.getId());
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void handleTripCancelled(TripSnapshotEvent event) {
        Booking booking = findBookingById(event.getId());
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    private void findDriverAndNotify(Booking booking) {
        String url = String.format(
            "%s/api/v1/location/nearby?requestId=%d&radiusKm=5&lng=%s&lat=%s&vehicleTypeId=%d",
            locationServiceUrl,
            booking.getId(),
            booking.getPickUpLongitude(),
            booking.getPickUpLatitude(),
            booking.getVehicleTypeId()
        );
        ResponseEntity<NearbyDriverResponse> res = restTemplate.getForEntity(url, NearbyDriverResponse.class);
        NearbyDriverResponse driver = res.getBody();
        if (driver == null || driver.getDriverId() == null) {
            return;
        }

        sendNotification(
            driver.getDriverId().toString(),
            "Co cuoc moi",
            "Ban co mot cuoc xe moi",
            Map.of(
                "tripId", booking.getTripId(),
                "customerId", booking.getCustomerId(),
                "paymentStatus", booking.getPaymentStatus(),
                "price", booking.getPrice()
            )
        );
    }

    private void publishPaymentSuccessEvent(Long tripId, String method) {
        TripStatusUpdateEvent event = TripStatusUpdateEvent.builder()
            .tripId(tripId)
            .status("PAID")
            .cancelReason(method)
            .build();
        outboxService.saveEvent(tripId.toString(), "BOOKING", "payment-success-topic", event);
    }

    private void sendNotification(String recipientId, String title, String content, Map<String, Object> data) {
        NotificationMessage message = NotificationMessage.builder()
            .recipientId(recipientId)
            .title(title)
            .content(content)
            .data(data)
            .eventId(UUID.randomUUID().toString())
            .build();
        restTemplate.postForEntity(notificationServiceUrl + "/api/v1/notifications", message, String.class);
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
            .bookingId(booking.getId())
            .tripId(booking.getTripId())
            .customerId(booking.getCustomerId())
            .driverId(booking.getDriverId())
            .vehicleTypeId(booking.getVehicleTypeId())
            .status(booking.getStatus().name())
            .paymentMethod(booking.getPaymentMethod().name())
            .paymentStatus(booking.getPaymentStatus())
            .checkoutUrl(booking.getPaymentCheckoutUrl())
            .price(booking.getPrice())
            .build();
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    private UserResponse getUser(Long id) {
        return restTemplate.getForObject(userServiceUrl + "/api/v1/user/" + id, UserResponse.class);
    }

    private DriverResponse getDriver(Long id) {
        return restTemplate.getForObject(driverServiceUrl + "/api/v1/drivers/" + id, DriverResponse.class);
    }

    private VehicleTypeResponse getVehicleType(Long vehicleTypeId) {
        ResponseEntity<List<VehicleTypeResponse>> response = restTemplate.exchange(
            vehicleServiceUrl + "/api/v1/vehicles/types",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
        );
        List<VehicleTypeResponse> types = response.getBody() == null ? List.of() : response.getBody();
        return types.stream()
            .filter(v -> Objects.equals(v.getId(), vehicleTypeId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Vehicle type not found"));
    }

    private void requireRole(String roles, String role) {
        if (roles == null || roles.isBlank()) {
            throw new IllegalArgumentException("Role " + role + " is required");
        }
        String normalized = roles.toUpperCase(Locale.ROOT);
        if ("CUSTOMER".equals(role)) {
            if (!normalized.contains("CUSTOMER") && !normalized.contains("USER")) {
                throw new IllegalArgumentException("Role " + role + " is required");
            }
            return;
        }
        if (!normalized.contains(role)) {
            throw new IllegalArgumentException("Role " + role + " is required");
        }
    }

    private void validateCoordinate(Double lng, Double lat) {
        if (lng == null || lat == null || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return BigDecimal.valueOf(r * c).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    private Long randomPositiveLong() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
