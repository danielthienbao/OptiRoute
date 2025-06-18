package com.optiroute.controller;

import com.optiroute.dto.UserDto;
import com.optiroute.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<UserDto.UserResponse> register(@Valid @RequestBody UserDto.RegisterRequest request) {
        UserDto.UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<UserDto.LoginResponse> login(@Valid @RequestBody UserDto.LoginRequest request) {
        // This will be handled by Spring Security and JWT filter
        // The actual login logic is in the JWT authentication filter
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get a new access token using refresh token")
    public ResponseEntity<UserDto.LoginResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // This will be handled by JWT service
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<Void> logout(Authentication authentication) {
        // Token invalidation logic will be handled by JWT service
        log.info("User {} logged out", authentication.getName());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    public ResponseEntity<UserDto.UserResponse> getCurrentUser(Authentication authentication) {
        UserDto.UserResponse user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update information for the currently authenticated user")
    public ResponseEntity<UserDto.UserResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserDto.UpdateUserRequest request) {
        UserDto.UserResponse currentUser = userService.getUserByUsername(authentication.getName());
        UserDto.UserResponse updatedUser = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for the currently authenticated user")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody UserDto.ChangePasswordRequest request) {
        UserDto.UserResponse currentUser = userService.getUserByUsername(authentication.getName());
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok().build();
    }
} 