package com.mywebsite.userservice.controller;

import com.mywebsite.userservice.model.response.UserResponse;
import com.mywebsite.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserInfoById(
        @PathVariable Long id
    ) {
        UserResponse res = userService.getUserInfoById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
