package com.mywebsite.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public java.util.Map<String, String> getHealth() {
        return java.util.Map.of("status", "ok");
    }
}
