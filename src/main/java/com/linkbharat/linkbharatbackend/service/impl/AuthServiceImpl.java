package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.RefreshToken;
import com.linkbharat.linkbharatbackend.domain.model.AuthResponse;
import com.linkbharat.linkbharatbackend.domain.model.LoginRequest;
import com.linkbharat.linkbharatbackend.domain.model.RefreshTokenRequest;
import com.linkbharat.linkbharatbackend.domain.model.RegisterRequest;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.security.JwtTokenProvider;
import com.linkbharat.linkbharatbackend.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;

    public AuthServiceImpl(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager,
                           RefreshTokenServiceImpl refreshTokenServiceImpl) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (authUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        AuthUser authUser = new AuthUser();
        authUser.setEmail(registerRequest.getEmail());
        authUser.setUsername(registerRequest.getUsername());
        authUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        AuthUser savedAuthUser = authUserRepository.save(authUser);

        // Generate tokens
        String token = jwtTokenProvider.generateToken(savedAuthUser);
        RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(savedAuthUser);

        return new AuthResponse(
                savedAuthUser.getId(),
                savedAuthUser.getUsername(),
                savedAuthUser.getEmail(),
                token,
                savedAuthUser.getRole().name(),
                refreshToken.getToken()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // 1️⃣ Validate input
//            if ((request.getEmail() == null || request.getEmail().isBlank()) &&
//                    (request.getUsername() == null || request.getUsername().isBlank())) {
//                throw new IllegalArgumentException("Email or Username must be provided");
//            }

            // 2️⃣ Determine identifier (prefer email)
            String identifier = (request.getEmail() != null && !request.getEmail().isBlank()) ? request.getEmail() : request.getUsername();

            // 3️⃣ Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));

            // 4️⃣ Set authenticated user context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 5️⃣ Retrieve user from repository
            AuthUser authUser = authUserRepository
                    .findByUsernameOrEmail(request.getUsername(), request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 6️⃣ Generate JWT Token and Refresh Token
            String token = jwtTokenProvider.generateToken(authUser);
            RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(authUser);

            // 7️⃣ Build and return response
            return new AuthResponse(
                    authUser.getId(),
                    authUser.getUsername(),
                    authUser.getEmail(),
                    token,
                    authUser.getRole().name(),
                    refreshToken.getToken()
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username/email or password");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal server error during login");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        RefreshToken oldToken = refreshTokenServiceImpl.findActiveByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenServiceImpl.verifyExpiration(oldToken);

        AuthUser authUser = oldToken.getAuthUser();

        refreshTokenServiceImpl.deleteByToken(oldToken);

        String newAccessToken = jwtTokenProvider.generateToken(authUser);
        RefreshToken newRefreshToken = refreshTokenServiceImpl.createRefreshToken(authUser);

        return new AuthResponse(
                authUser.getId(),
                authUser.getUsername(),
                authUser.getEmail(),
                newAccessToken,
                authUser.getRole().name(),
                newRefreshToken.getToken()
        );
    }


    @Override
    @Transactional
    public void logout(String refreshToken) {
        // Find the refresh token
        Optional<RefreshToken> tokenOpt = refreshTokenServiceImpl.findByToken(refreshToken);

        // If token exists, delete it using the repository directly
        tokenOpt.ifPresent(token -> {
            // Use the repository to delete the token
            refreshTokenServiceImpl.deleteByToken(token);
        });
    }
    public AuthResponse getCurrentUser(String bearerToken) {
        // 1. Robustly extract the token
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = bearerToken.substring(7).trim();

        // 2. Validate and extract
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Use the same lookup logic as login
            AuthUser authUser = authUserRepository.findByUsername(username)
                    .or(() -> authUserRepository.findByEmail(username))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new AuthResponse(
                    authUser.getId(),
                    authUser.getUsername(),
                    authUser.getEmail(),
                    token,
                    authUser.getRole().name(),
                    null // Refresh token usually not needed for /me
            );
        }
        throw new RuntimeException("Token validation failed");
    }
}