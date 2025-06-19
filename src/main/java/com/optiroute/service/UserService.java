package com.optiroute.service;

import com.optiroute.dto.UserDto;
import com.optiroute.dto.UserCreateRequest;
import com.optiroute.dto.UserUpdateRequest;
import com.optiroute.exception.ResourceNotFoundException;
import com.optiroute.model.User;
import com.optiroute.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    // New methods for UserController
    
    public Page<UserDto> getAllUsers(User.Role role, Boolean active, String search, Pageable pageable) {
        if (role != null && active != null && search != null) {
            return userRepository.findByRoleAndActiveAndSearch(role, active, search, pageable)
                    .map(this::convertToDto);
        } else if (role != null && active != null) {
            return userRepository.findByRoleAndActive(role, active, pageable)
                    .map(this::convertToDto);
        } else if (role != null && search != null) {
            return userRepository.findByRoleAndSearch(role, search, pageable)
                    .map(this::convertToDto);
        } else if (active != null && search != null) {
            return userRepository.findByActiveAndSearch(active, search, pageable)
                    .map(this::convertToDto);
        } else if (role != null) {
            return userRepository.findByRole(role, pageable)
                    .map(this::convertToDto);
        } else if (active != null) {
            return userRepository.findByActive(active, pageable)
                    .map(this::convertToDto);
        } else if (search != null) {
            return userRepository.findBySearch(search, pageable)
                    .map(this::convertToDto);
        } else {
            return userRepository.findAll(pageable)
                    .map(this::convertToDto);
        }
    }
    
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }
    
    public UserDto createUser(UserCreateRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .isActive(request.getActive() != null ? request.getActive() : true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());
        
        return convertToDto(savedUser);
    }
    
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (request.getUsername() != null) {
            // Check if username is already taken by another user
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setIsActive(request.getActive());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser.getUsername());
        
        return convertToDto(updatedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }
    
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        log.info("Activated user: {}", savedUser.getUsername());
        
        return convertToDto(savedUser);
    }
    
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(false);
        User savedUser = userRepository.save(user);
        log.info("Deactivated user: {}", savedUser.getUsername());
        
        return convertToDto(savedUser);
    }
    
    public UserDto changeUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setRole(role);
        User savedUser = userRepository.save(user);
        log.info("Changed role for user {} to {}", savedUser.getUsername(), role);
        
        return convertToDto(savedUser);
    }
    
    public List<UserDto> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public List<UserDto> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public List<UserDto> getActiveUsers() {
        return userRepository.findByActive(true).stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public List<UserDto> getInactiveUsers() {
        return userRepository.findByActive(false).stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public String resetUserPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Generate temporary password
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        
        log.info("Reset password for user: {}", user.getUsername());
        return temporaryPassword;
    }
    
    public List<UserDto> getAvailableDrivers() {
        // This would need to be implemented based on route assignment logic
        // For now, return all active drivers
        return userRepository.findActiveDrivers().stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public UserController.UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);
        long adminUsers = userRepository.countByRole(User.Role.ADMIN);
        long driverUsers = userRepository.countByRole(User.Role.DRIVER);
        
        // These would need custom repository methods for date-based queries
        long usersCreatedThisMonth = userRepository.countByCreatedAtAfter(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
        long usersCreatedThisWeek = userRepository.countByCreatedAtAfter(
                LocalDateTime.now().minusWeeks(1));
        
        return UserController.UserStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .adminUsers(adminUsers)
                .driverUsers(driverUsers)
                .usersCreatedThisMonth(usersCreatedThisMonth)
                .usersCreatedThisWeek(usersCreatedThisWeek)
                .build();
    }
    
    // Legacy methods for backward compatibility
    
    public UserDto.UserResponse createUser(UserDto.RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : User.Role.DRIVER)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());
        
        return UserDto.UserResponse.fromUser(savedUser);
    }
    
    public UserDto.UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        return UserDto.UserResponse.fromUser(user);
    }
    
    public List<UserDto.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto.UserResponse::fromUser)
                .toList();
    }
    
    public List<UserDto.UserResponse> getActiveDrivers() {
        return userRepository.findActiveDrivers().stream()
                .map(UserDto.UserResponse::fromUser)
                .toList();
    }
    
    public UserDto.UserResponse updateUser(Long id, UserDto.UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser.getUsername());
        
        return UserDto.UserResponse.fromUser(updatedUser);
    }
    
    public void changePassword(Long id, UserDto.ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed for user: {}", user.getUsername());
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Helper method to convert User to UserDto
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 