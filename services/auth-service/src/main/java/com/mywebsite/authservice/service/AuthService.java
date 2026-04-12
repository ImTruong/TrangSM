package com.mywebsite.authservice.service;

import com.mywebsite.authservice.model.request.LoginRequest;

public interface AuthService {
    String login(LoginRequest req);

    void reset(Long id);
}
