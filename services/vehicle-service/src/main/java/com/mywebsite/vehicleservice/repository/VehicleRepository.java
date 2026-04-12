package com.mywebsite.vehicleservice.repository;

import com.mywebsite.vehicleservice.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    java.util.Optional<Vehicle> findFirstByOwnerId(Long ownerId);
}
