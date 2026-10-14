package com.bank.bankapplication.dto.response;

public class RegisterResponse {

    private String message;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;

    public RegisterResponse(
            String message,
            Long userId,
            String firstName,
            String lastName,
            String email,
            String mobileNumber) {

        this.message = message;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}