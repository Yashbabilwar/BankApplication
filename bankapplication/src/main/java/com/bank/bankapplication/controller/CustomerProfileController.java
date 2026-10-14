package com.bank.bankapplication.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerProfileController {
    @GetMapping("/profile")
    public Map<String, String> profile(Authentication authentication) {
        return Map.of(
                "message", "Protected API accessed successfully",
                "email", authentication.getName());
    }
}
