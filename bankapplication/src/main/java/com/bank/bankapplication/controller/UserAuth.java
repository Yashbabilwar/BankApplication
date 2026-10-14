package com.bank.bankapplication.controller;


import com.bank.bankapplication.dto.request.LoginRequest;
import com.bank.bankapplication.dto.response.LoginResponse;
import com.bank.bankapplication.dto.request.RegisterRequest;
import com.bank.bankapplication.dto.response.RegisterResponse;
import com.bank.bankapplication.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuth {

    private final AuthService authService;

    public UserAuth(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}
//Test case	Result
//Customer Registration	✅ PASS
//Duplicate Mobile Number	✅ PASS
//Correct Password Login	✅ PASS
//Wrong Password Login	✅ PASS
//JWT Token Generation