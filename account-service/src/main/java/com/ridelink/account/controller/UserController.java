package com.ridelink.account.controller;

import com.ridelink.account.dto.UpdateUserRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.exception.ApiException;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.AuthHelper;
import com.ridelink.account.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Profile viewing and updating")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get a user profile (self or ADMIN)")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable String id, HttpServletRequest request) {
        // Users can view their own profile; admins can view any profile.
        AuthenticatedUser authUser = AuthHelper.requireAuth(request);
        if (!authUser.getId().equals(id) && !"ADMIN".equals(authUser.getRole())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.from(user);
    }

    @Operation(summary = "Update own profile name")
    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable String id, @RequestBody UpdateUserRequest req,
            HttpServletRequest request) {
        // Profile edits are limited to the account owner.
        AuthenticatedUser authUser = AuthHelper.requireAuth(request);
        if (!authUser.getId().equals(id)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        // A missing name leaves the existing profile name unchanged.
        if (req.getName() != null) {
            user.setName(req.getName());
        }
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Operation(summary = "Suspend or reactivate a user (ADMIN only)")
    @PatchMapping("/{id}/status")
    public UserResponse updateStatus(@PathVariable String id, @RequestBody java.util.Map<String, String> body,
            HttpServletRequest request) {
        // Only admins may change another user's account status.
        AuthenticatedUser authUser = AuthHelper.requireAuth(request);
        AuthHelper.requireRole(authUser, "ADMIN");

        // Accept only the account states supported by this endpoint.
        String status = body.get("status");
        if (!java.util.Set.of("ACTIVE", "SUSPENDED").contains(status)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "status must be ACTIVE or SUSPENDED");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setStatus(status);
        user = userRepository.save(user);
        return UserResponse.from(user);
    }
}