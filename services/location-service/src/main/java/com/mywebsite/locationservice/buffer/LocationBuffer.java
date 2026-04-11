package com.mywebsite.locationservice.buffer;

import com.mywebsite.locationservice.model.request.LocationRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationBuffer {
    private final List<LocationRequest> buffer = new ArrayList<>();

    public synchronized boolean addAndCheckFull(LocationRequest req, int maxsize) {
        buffer.add(req);
        return buffer.size() >= maxsize;
    }

    public synchronized List<LocationRequest> drain(int maxSize) {
        int size = Math.min(maxSize, buffer.size());

        List<LocationRequest> batch = new ArrayList<>(buffer.subList(0, size));
        buffer.subList(0, size).clear();

        return batch;
    }

    public synchronized int size() {
        return buffer.size();
    }
}
