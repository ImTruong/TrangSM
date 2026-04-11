package com.mywebsite.locationservice.worker;

import com.mywebsite.locationservice.constant.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DriverCleanupWorker {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.worker.time-out}")
    private long TIMEOUT_MS;

    @Scheduled(fixedDelayString = "${app.worker.cleanup-interval}")
    public void cleanup() {
        long now = System.currentTimeMillis();
        long threshold = now - TIMEOUT_MS;

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        // 1. Lấy driver hết hạn
        Set<String> expiredDrivers =
            zSetOps.rangeByScore(RedisKeys.ZSET_LAST_SEEN, 0, threshold);

        if (expiredDrivers == null || expiredDrivers.isEmpty()) {
            return;
        }

        byte[] lastSeenKey = RedisKeys.ZSET_LAST_SEEN.getBytes();

        // 2. Remove batch (pipeline)
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

            for (String memberStr : expiredDrivers) {

                String[] parts = memberStr.split(":");
                String driverId = parts[0];
                String vehicleTypeId = parts[1];

                byte[] member = memberStr.getBytes();

                byte[] geoAvailableKey =
                    (RedisKeys.GEO_AVAILABLE + vehicleTypeId).getBytes();

                byte[] geoAllKey =
                    (RedisKeys.GEO_ALL + vehicleTypeId).getBytes();

                connection.zSetCommands().zRem(geoAvailableKey, member);
                connection.zSetCommands().zRem(geoAllKey, member);
                connection.zSetCommands().zRem(lastSeenKey, member);
            }

            return null;
        });

        System.out.println("Removed " + expiredDrivers.size() + " drivers");
    }
}
