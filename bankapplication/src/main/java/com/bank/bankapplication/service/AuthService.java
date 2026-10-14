package com.bank.bankapplication.service;

import com.bank.bankapplication.dto.request.LoginRequest;
import com.bank.bankapplication.dto.response.LoginResponse;
import com.bank.bankapplication.dto.request.RegisterRequest;
import com.bank.bankapplication.dto.response.RegisterResponse;

public interface AuthService {
//               RegisterRequest is data type and DTO class
    RegisterResponse register(RegisterRequest request);
/*   return type      method    parameter       reference */
    LoginResponse login(LoginRequest request);
}
