package com.mywebsite.authservice.controller;

import com.mywebsite.authservice.model.request.LoginRequest;
import com.mywebsite.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(
        @RequestBody LoginRequest req
    ) {
        String token = authService.login(req);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<String> resetPassword(
        @PathVariable Long id
    ) {
        authService.reset(id);
        return new ResponseEntity<>("Password reset to: 123456", HttpStatus.OK);
    }
}
