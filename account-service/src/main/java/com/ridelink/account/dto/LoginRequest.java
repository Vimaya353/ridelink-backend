package com.ridelink.account.dto;

// Import validation annotation to enforce non-blank fields in request body
import jakarta.validation.constraints.NotBlank;

// DTO (Data Transfer Object) representing the JSON payload for a login request
public class LoginRequest {

    // Email field; must not be blank when validating the request
    @NotBlank(message = "email is required")
    private String email;

    // Password field; must not be blank when validating the request
    @NotBlank(message = "password is required")
    private String password;

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}