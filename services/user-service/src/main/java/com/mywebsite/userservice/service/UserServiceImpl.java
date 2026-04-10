package com.mywebsite.userservice.service;

import com.mywebsite.userservice.exception.UserNotFoundException;
import com.mywebsite.userservice.model.entity.User;
import com.mywebsite.userservice.model.response.UserResponse;
import com.mywebsite.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserInfoById(long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .phoneNumber(user.getPhoneNumber())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }
}
