package com.bank.bankapplication.dto.response;

public class LoginResponse {

    private String message;
    private Long userId;
    private String email;
    private String role;
    private String token;

    public LoginResponse(
            String message,
            Long userId,
            String email,
            String role,
            String token) {

        this.message = message;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}