package com.bank.bankapplication.service;

import com.bank.bankapplication.dto.LoginRequest;
import com.bank.bankapplication.dto.LoginResponse;
import com.bank.bankapplication.dto.RegisterRequest;
import com.bank.bankapplication.dto.RegisterResponse;
import com.bank.bankapplication.entity.User;
import com.bank.bankapplication.exception.EmailAlreadyExistsException;
import com.bank.bankapplication.exception.InvalidCredentialsException;
import com.bank.bankapplication.exception.MobileAlreadyExistsException;
import com.bank.bankapplication.repository.UserRepository;
import com.bank.bankapplication.security.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String mobile = request.getMobileNumber().trim();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        if (userRepository.existsByMobileNumber(mobile)) {
            throw new MobileAlreadyExistsException("Mobile number already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setMobileNumber(mobile);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return new RegisterResponse(
                    "Customer registered successfully", savedUser.getId(),
                    savedUser.getFirstName(), savedUser.getLastName(),
                    savedUser.getEmail(), savedUser.getMobileNumber());
        } catch (DataIntegrityViolationException ex) {
            // Database unique constraints are the final guard against concurrent duplicate requests.
            String message = ex.getMostSpecificCause().getMessage();
            if (message != null && message.toLowerCase(Locale.ROOT).contains("email")) {
                throw new EmailAlreadyExistsException("Email already registered");
            }
            if (message != null && (message.toLowerCase(Locale.ROOT).contains("mobile")
                    || message.toLowerCase(Locale.ROOT).contains("mobile_number"))) {
                throw new MobileAlreadyExistsException("Mobile number already registered");
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse("Login successful", user.getId(),
                user.getEmail(), user.getRole(), token);
    }
}
