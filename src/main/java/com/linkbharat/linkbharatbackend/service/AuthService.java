package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.AuthResponse;
import com.linkbharat.linkbharatbackend.domain.model.LoginRequest;
import com.linkbharat.linkbharatbackend.domain.model.RefreshTokenRequest;
import com.linkbharat.linkbharatbackend.domain.model.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse getCurrentUser(String token);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(String refreshToken);
}
