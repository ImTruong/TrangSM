package com.mywebsite.locationservice.repository;

import com.mywebsite.locationservice.model.request.LocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocationLogRepositoryImpl implements LocationLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<LocationRequest> list) {
        String sql = """
                INSERT INTO location_log (driver_id, lat, lng, vehicle_type_id, timestamp)
                VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, list, list.size(),
            (ps, argument) -> {
                ps.setLong(1, argument.getDriverId());
                ps.setBigDecimal(2, argument.getLat());
                ps.setBigDecimal(3, argument.getLng());
                ps.setLong(4, argument.getVehicleTypeId());
                ps.setTimestamp(5, Timestamp.from(Instant.now()));
            });
    }
}
