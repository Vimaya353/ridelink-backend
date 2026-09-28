package com.ridelink.account.controller;

import com.ridelink.account.dto.*;
import com.ridelink.account.exception.ApiException;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Registration, login and JWT issuance")
public class AuthController {

    // Roles that a user can select during registration.
    private static final Set<String> VALID_ROLES = Set.of("PASSENGER", "DRIVER", "ADMIN");

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Register a new passenger, driver or admin account")
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        // Check that the requested role is supported.
        if (!VALID_ROLES.contains(req.getRole())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "role must be PASSENGER, DRIVER or ADMIN");
        }

        // Do not register another account with the same email.
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }

        // Hash the password before saving the new user.
        User user = new User(req.getName(), req.getEmail(),
                passwordEncoder.encode(req.getPassword()), req.getRole());
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Operation(summary = "Login and receive a JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        // Use the same error message for an unknown email and a wrong password.
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // Suspended users cannot log in.
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Compare the submitted password with the stored password hash.
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Return a JWT and the user's public account details.
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getEmail());
        return new AuthResponse(token, UserResponse.from(user));
    }
}