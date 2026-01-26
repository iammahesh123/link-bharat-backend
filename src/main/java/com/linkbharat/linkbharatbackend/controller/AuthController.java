package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.AuthResponse;
import com.linkbharat.linkbharatbackend.domain.model.LoginRequest;
import com.linkbharat.linkbharatbackend.domain.model.RefreshTokenRequest;
import com.linkbharat.linkbharatbackend.domain.model.RegisterRequest;
import com.linkbharat.linkbharatbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Handles user registration, login, token refresh, and profile retrieval")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details. Returns a JWT token upon successful registration.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User registration details",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "username": "john_doe",
                              "email": "john@example.com",
                              "password": "StrongPass123!",
                              "confirmPassword": "StrongPass123!"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Email or username already exists", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Operation(
            summary = "Authenticate user and get tokens",
            description = "Authenticates the user with email and password. Returns access and refresh tokens on success.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "email": "john@example.com",
                              "password": "StrongPass123!"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Refresh expired access token",
            description = "Generates a new access token using a valid refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Refresh token details",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5..."
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content)
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @org.springframework.web.bind.annotation.RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse response = authService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @Operation(
            summary = "Get current authenticated user",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String bearerToken
    ) {
        // Debug line: Print this to your console to see if the token is even arriving
        System.out.println("Received Token: " + bearerToken);

        if (bearerToken == null || bearerToken.isBlank()) {
            // Instead of just .build(), return a body or let the service handle it
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(bearerToken));
    }
}
