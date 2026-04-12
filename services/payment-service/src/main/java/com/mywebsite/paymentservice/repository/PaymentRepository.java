package com.mywebsite.paymentservice.repository;

import com.mywebsite.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByStripeSessionId(String stripeSessionId);
    Optional<Payment> findByTripId(Long tripId);
}
