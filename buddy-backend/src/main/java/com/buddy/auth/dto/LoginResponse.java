package com.buddy.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String firstName,
        String lastName,
        String email
) {
}
