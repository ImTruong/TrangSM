package com.mywebsite.locationservice.service;

import com.mywebsite.locationservice.buffer.LocationBuffer;
import com.mywebsite.locationservice.constant.RedisKeys;
import com.mywebsite.locationservice.model.event.DriverAcceptedEvent;
import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.model.request.NearbyRequest;
import com.mywebsite.locationservice.model.response.NearbyDriver;
import com.mywebsite.locationservice.worker.LocationFlushWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final LocationBuffer buffer;
    private final LocationFlushWorker locationFlushWorker;

    @Value("${app.buffer.max-size}")
    private int maxSize;

    @Override
    public void updateLocation(LocationRequest req) {

        long timestamp = Instant.now().toEpochMilli();

//        ADD VỊ TRÍ VÀO REDIS
//        DÙNG PIPELINE ĐỂ KẾT NỐI ĐẾN REDIS 1 LẦN
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] geoAllKey = (RedisKeys.GEO_ALL + req.getVehicleTypeId()).getBytes();
            byte[] geoAvailableKey = (RedisKeys.GEO_AVAILABLE + req.getVehicleTypeId()).getBytes();
            byte[] lastSeenKey = RedisKeys.ZSET_LAST_SEEN.getBytes();

//            THÊM CẢ VEHICLE_TYPE_ID ĐỂ CLEANUP LẤY ĐƯỢC VEHICLE TYPE ID
            byte[] member = (req.getDriverId() + ":" + req.getVehicleTypeId()).getBytes();

            RedisGeoCommands.GeoLocation<byte[]> location = new RedisGeoCommands.GeoLocation<>(
                member,
                new Point(req.getLng(), req.getLat())
            );

//            UPDATE TẤT CẢ
            connection.geoAdd(geoAllKey, location);

//            UPDATE AVAILABLE
            if (req.getStatus().equalsIgnoreCase("available")) {
                connection.geoAdd(geoAvailableKey, location);
            } else {
                connection.zSetCommands().zRem(
                    geoAvailableKey, member
                );
            }

//            UPDATE TIMESTAMP
            connection.zAdd(lastSeenKey, timestamp, member);
            return null;
        });

//        ADD VÀO BUFFER VÀ TRIGGER FLUSH
        boolean isFull = buffer.addAndCheckFull(req, maxSize);

        if (isFull) {
            locationFlushWorker.flushAsync();
        }
    }

    @Override
    public NearbyDriver getClosestDriver(NearbyRequest req) {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

//        GEOSEARCH TÌM DANH SÁCH NHỮNG TÀI XẾ Ở GẦN
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
            geoOps.search(
                RedisKeys.GEO_AVAILABLE + req.getVehicleTypeId(),
                GeoReference.fromCoordinate(req.getLng(), req.getLat()),
                new Distance(req.getRadiusKm(), Metrics.KILOMETERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                    .includeCoordinates()
                    .sortAscending()
                    .limit(req.getLimit())
            );

        if (results == null) return null;

        List<NearbyDriver> nearbyResponses = results.getContent().stream().map(
            r -> NearbyDriver.builder()
                .driverId(Long.valueOf(r.getContent().getName().split(":")[0]))
                .lng(r.getContent().getPoint().getX())
                .lat(r.getContent().getPoint().getY())
                .build()
        ).toList();

//        VỚI MỖI TÀI XẾ, KIỂM TRA XEM CÓ AI ĐẶT CHƯA
        for (NearbyDriver n : nearbyResponses) {
            if (tryLockDriver(n, req.getRequestId())) {
//                TODO: BĂN NOTI SANG CHO TÀI XẾ
                return n;
            }
        }

        return null;
    }

    @Override
    public void handleDriverAccepted(DriverAcceptedEvent event) {
        String geoAvailableKey = RedisKeys.GEO_AVAILABLE + event.getVehicleTypeId();
        String member = event.getDriverId() + ":" + event.getVehicleTypeId();
        redisTemplate.opsForZSet().remove(geoAvailableKey, member);
        System.out.println(event.getDriverId() + ":" + event.getVehicleTypeId());
    }

    public boolean tryLockDriver(NearbyDriver driver, Long requestId) {
//        NẾU SET ĐƯỢC -> CHƯA AI ĐẶT -> OK
//        SET TTL = 30S -> TỰ XOÁ KEY DÙ SERVICE CHẾT
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(
                "lock:driver:" + driver.getDriverId(),
                String.valueOf(requestId),
                Duration.ofSeconds(30));

        return Boolean.TRUE.equals(success);
    }
}
