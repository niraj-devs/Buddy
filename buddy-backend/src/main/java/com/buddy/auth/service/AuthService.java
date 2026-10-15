package com.buddy.auth.service;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;
import com.buddy.auth.dto.RegisterRequest;
import com.buddy.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshToken);

    void logout(String refreshToken);
}

