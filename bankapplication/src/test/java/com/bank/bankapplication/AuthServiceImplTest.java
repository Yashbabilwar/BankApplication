package com.bank.bankapplication;

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
import com.bank.bankapplication.service.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthServiceImpl(
                userRepository,
                passwordEncoder,
                jwtUtil
        );
    }

    private RegisterRequest createRegisterRequest(
            String email, String mobile) {

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(email);
        request.setMobileNumber(mobile);
        request.setPassword("Password@123");

        return request;
    }

    private LoginRequest createLoginRequest(
            String email, String password) {

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    // 1. Registration and BCrypt password hashing
    @Test
    void registerHashesPasswordSuccessfully() {

        RegisterRequest request = createRegisterRequest(
                "testuser@example.com", "9876543210");

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByMobileNumber("9876543210"))
                .thenReturn(false);

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(101L);
                    return user;
                });

        RegisterResponse response = authService.register(request);

        assertEquals(101L, response.getUserId());
        assertEquals("testuser@example.com", response.getEmail());

        verify(userRepository).saveAndFlush(argThat(user ->
                !user.getPassword().equals("Password@123")
                        && passwordEncoder.matches(
                        "Password@123", user.getPassword())
        ));
    }

    // 2. Duplicate email is rejected
    @Test
    void duplicateEmailIsRejected() {

        RegisterRequest request = createRegisterRequest(
                "testuser@example.com", "9876543210");

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .saveAndFlush(any(User.class));
    }

    // 3. Duplicate mobile number is rejected
    @Test
    void duplicateMobileNumberIsRejected() {

        RegisterRequest request = createRegisterRequest(
                "newuser@example.com", "9876543210");

        when(userRepository.existsByEmail("newuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByMobileNumber("9876543210"))
                .thenReturn(true);

        assertThrows(
                MobileAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .saveAndFlush(any(User.class));
    }

    // 4. Login succeeds with correct password
    @Test
    void loginReturnsJwtForCorrectPassword() {

        User user = new User();
        user.setId(101L);
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(
                "testuser@example.com", "CUSTOMER"))
                .thenReturn("test.jwt.token");

        LoginResponse response = authService.login(
                createLoginRequest(
                        "testuser@example.com", "Password@123"));

        assertEquals("test.jwt.token", response.getToken());
        assertEquals(101L, response.getUserId());

        verify(jwtUtil).generateToken(
                "testuser@example.com", "CUSTOMER");
    }

    // 5. Wrong password is rejected
    @Test
    void loginRejectsWrongPassword() {

        User user = new User();
        user.setId(101L);
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        createLoginRequest(
                                "testuser@example.com",
                                "WrongPassword@123"))
        );

        verify(jwtUtil, never())
                .generateToken(any(), any());
    }

    // 6. Unknown email is rejected
    @Test
    void loginRejectsUnknownEmail() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        createLoginRequest(
                                "unknown@example.com",
                                "Password@123"))
        );

        verify(jwtUtil, never())
                .generateToken(any(), any());
    }

    // 7. Email is trimmed and converted to lowercase
    @Test
    void registerNormalizesEmail() {

        RegisterRequest request = createRegisterRequest(
                "  TESTUSER@EXAMPLE.COM  ", "9876543210");

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByMobileNumber("9876543210"))
                .thenReturn(false);

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(102L);
                    return user;
                });

        RegisterResponse response = authService.register(request);

        assertEquals("testuser@example.com", response.getEmail());

        verify(userRepository).saveAndFlush(argThat(user ->
                "testuser@example.com".equals(user.getEmail())
        ));
    }

    // 8. Database email constraint violation is translated
    @Test
    void registerTranslatesDatabaseEmailConstraintViolation() {

        RegisterRequest request = createRegisterRequest(
                "testuser@example.com", "9876543210");

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByMobileNumber("9876543210"))
                .thenReturn(false);

        DataIntegrityViolationException databaseException =
                new DataIntegrityViolationException(
                        "constraint violation",
                        new RuntimeException("email unique constraint"));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(databaseException);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );
    }
}