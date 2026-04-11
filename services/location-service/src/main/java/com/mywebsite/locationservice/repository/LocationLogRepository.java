package com.mywebsite.locationservice.repository;

import com.mywebsite.locationservice.model.request.LocationRequest;

import java.util.List;

public interface LocationLogRepository {
    void batchInsert(List<LocationRequest> list);
}
