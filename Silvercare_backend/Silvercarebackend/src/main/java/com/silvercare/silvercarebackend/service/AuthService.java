package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.dto.AuthRequest;
import com.silvercare.silvercarebackend.dto.AuthResponse;
import com.silvercare.silvercarebackend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
