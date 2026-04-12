package com.mywebsite.userservice.service;

import com.mywebsite.userservice.model.response.UserResponse;

public interface UserService {
    UserResponse getUserInfoById(Long id);
}
