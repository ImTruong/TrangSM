package com.mywebsite.locationservice.worker;

import com.mywebsite.locationservice.buffer.LocationBuffer;
import com.mywebsite.locationservice.model.request.LocationRequest;
import com.mywebsite.locationservice.repository.LocationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocationFlushWorker {
    private final LocationBuffer buffer;
    private final LocationLogRepository locationLogRepository;

    private final AtomicBoolean isFlushing = new AtomicBoolean(false);

    @Value("${app.buffer.max-size}")
    private int batchSize;

    // chạy định kỳ
    @Scheduled(fixedDelayString = "${app.buffer.flush-interval}")
    public void flush() {
        doFlush();
    }

    // chạy khi buffer đầy
    @Async
    public void flushAsync() {
        doFlush();
    }

    public void doFlush() {
        if (!isFlushing.compareAndSet(false, true)) {
            return; // đã có thread khác đang flush
        }

        try {
            List<LocationRequest> batch = buffer.drain(batchSize);

            if (!batch.isEmpty()) {
                locationLogRepository.batchInsert(batch);

                System.out.println("Flushed: " + batch.size());
            }
        } finally {
            isFlushing.set(false);
        }
    }
}
