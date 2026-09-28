package com.ridelink.account.dto;

import com.ridelink.account.model.User;

import java.time.Instant;

// Represents the user details returned by account-service endpoints.
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private String status;
    private Instant createdAt;

    // Copy only response fields from the stored user; do not expose the password
    // hash.
    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.id = u.getId();
        r.name = u.getName();
        r.email = u.getEmail();
        r.role = u.getRole();
        r.status = u.getStatus();
        r.createdAt = u.getCreatedAt();
        return r;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}