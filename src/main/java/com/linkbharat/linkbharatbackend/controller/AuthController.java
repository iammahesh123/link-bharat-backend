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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RestControllerAdvice
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
            requestBody = @RequestBody(
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Operation(
            summary = "Authenticate user and get tokens",
            description = "Authenticates the user with email and password. Returns access and refresh tokens on success.",
            requestBody = @RequestBody(
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Refresh expired access token",
            description = "Generates a new access token using a valid refresh token.",
            requestBody = @RequestBody(
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
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse response = authService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(
            summary = "Get current authenticated user",
            description = "Returns details of the currently authenticated user based on the provided JWT token.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token (format: 'Bearer <token>')",
                            required = true,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5..."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content)
            }
    )
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getCurrentUser(token));
    }
}
