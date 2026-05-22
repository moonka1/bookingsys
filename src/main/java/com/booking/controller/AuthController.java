package com.booking.controller;

import com.booking.dto.auth.AuthResponse;
import com.booking.dto.auth.LoginRequest;
import com.booking.dto.user.UserCreateRequest;
import com.booking.dto.user.UserResponse;
import com.booking.entity.User;
import com.booking.exception.InvalidRequestException;
import com.booking.security.JwtUtil;
import com.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserCreateRequest request) {
        log.info("New registration request for email: {}", request.getEmail());
        UserResponse userResponse = userService.createUser(request);
        
        String token = jwtUtil.generateToken(userResponse.getEmail(), userResponse.getRole().toString());
        
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .email(userResponse.getEmail())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .role(userResponse.getRole().toString())
                .userId(userResponse.getId())
                .message("User registered successfully")
                .build();
        
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            UserResponse userResponse = userService.getUserByEmail(request.getEmail());
            
            if (!passwordEncoder.matches(request.getPassword(), 
                    userService.getUserEntityById(userResponse.getId()).getPassword())) {
                log.warn("Invalid password for email: {}", request.getEmail());
                throw new InvalidRequestException("Invalid email or password");
            }
            
            String token = jwtUtil.generateToken(userResponse.getEmail(), userResponse.getRole().toString());
            
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .email(userResponse.getEmail())
                    .firstName(userResponse.getFirstName())
                    .lastName(userResponse.getLastName())
                    .role(userResponse.getRole().toString())
                    .userId(userResponse.getId())
                    .message("Login successful")
                    .build();
            
            log.info("User {} logged in successfully", request.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw new InvalidRequestException("Invalid email or password");
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.isTokenValid(token);
            return ResponseEntity.ok(isValid);
        }
        return ResponseEntity.ok(false);
    }
}
