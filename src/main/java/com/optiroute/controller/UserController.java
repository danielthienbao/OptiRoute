package com.optiroute.controller;

import com.optiroute.dto.UserDto;
import com.optiroute.dto.UserCreateRequest;
import com.optiroute.dto.UserUpdateRequest;
import com.optiroute.model.User;
import com.optiroute.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users (Admin only)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @Parameter(description = "Role filter") @RequestParam(required = false) User.Role role,
            @Parameter(description = "Active status filter") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Search query") @RequestParam(required = false) String search,
            @Parameter(description = "Pageable parameters") Pageable pageable) {
        
        Page<UserDto> users = userService.getAllUsers(role, active, search, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User data") @Valid @RequestBody UserCreateRequest request) {
        
        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Updated user data") @Valid @RequestBody UserUpdateRequest request) {
        
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate user", description = "Activate a deactivated user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        UserDto activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(activatedUser);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        UserDto deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(deactivatedUser);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Change user role", description = "Change the role of a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> changeUserRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New role") @RequestParam User.Role role) {
        
        UserDto updatedUser = userService.changeUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name, email, or username")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> searchUsers(
            @Parameter(description = "Search query") @RequestParam String query) {
        
        List<UserDto> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users with a specific role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable User.Role role) {
        
        List<UserDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Get all active users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        
        List<UserDto> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive users", description = "Get all inactive users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getInactiveUsers() {
        
        List<UserDto> users = userService.getInactiveUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset user password", description = "Reset a user's password to a temporary one")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetUserPassword(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        String temporaryPassword = userService.resetUserPassword(id);
        return ResponseEntity.ok("Password reset successfully. Temporary password: " + temporaryPassword);
    }

    @GetMapping("/drivers")
    @Operation(summary = "Get all drivers", description = "Get all users with DRIVER role")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<UserDto>> getAllDrivers() {
        
        List<UserDto> drivers = userService.getUsersByRole(User.Role.DRIVER);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/available")
    @Operation(summary = "Get available drivers", description = "Get all active drivers who are not currently assigned to routes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<UserDto>> getAvailableDrivers() {
        
        List<UserDto> availableDrivers = userService.getAvailableDrivers();
        return ResponseEntity.ok(availableDrivers);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get user statistics for admin dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStats> getUserStats() {
        
        UserStats stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long inactiveUsers;
        private Long adminUsers;
        private Long driverUsers;
        private Long usersCreatedThisMonth;
        private Long usersCreatedThisWeek;
    }
} 