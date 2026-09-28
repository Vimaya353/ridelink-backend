package com.ridelink.account.dto;

// Returns the JWT alongside the authenticated user's public profile.
public class AuthResponse {
    private String token;
    private UserResponse user;

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    // Accessors allow the response fields to be serialized as JSON.
    public String getToken() {
        return token;
    }

    public UserResponse getUser() {
        return user;
    }
}