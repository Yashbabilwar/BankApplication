package com.bank.bankapplication.service;

import com.bank.bankapplication.dto.LoginRequest;
import com.bank.bankapplication.dto.LoginResponse;
import com.bank.bankapplication.dto.RegisterRequest;
import com.bank.bankapplication.dto.RegisterResponse;

public interface AuthService {
//               RegisterRequest is data type and DTO class
    RegisterResponse register(RegisterRequest request);
/*   return type      method    parameter       reference */
    LoginResponse login(LoginRequest request);
}
