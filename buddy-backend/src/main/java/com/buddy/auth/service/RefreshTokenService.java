package com.buddy.auth.service;

import com.buddy.auth.entity.RefreshToken;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenTtlDays;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${buddy.auth.refresh-token-ttl-days:7}")
            long refreshTokenTtlDays) {

        if (refreshTokenTtlDays <= 0) {
            throw new IllegalArgumentException(
                    "Refresh-token lifetime must be positive");
        }

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    @Transactional
    public String createRefreshToken(User user) {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(refreshTokenTtlDays));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public String rotateRefreshToken(String rawToken) {
        RefreshToken storedToken = validateRefreshTokenForUpdate(rawToken);

        storedToken.setRevoked(true);
        refreshTokenRepository.saveAndFlush(storedToken);

        return createRefreshToken(storedToken.getUser());
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String rawToken) {
        return validate(rawToken, false);
    }

    @Transactional
    public RefreshToken validateRefreshTokenForUpdate(String rawToken) {
        return validate(rawToken, true);
    }

    private RefreshToken validate(String rawToken, boolean lockForUpdate) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required");
        }

        String tokenHash = hashToken(rawToken);

        RefreshToken storedToken = (lockForUpdate
                ? refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                : refreshTokenRepository.findByTokenHash(tokenHash))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh token has been revoked");
        }

        if (!storedToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Refresh token has expired");
        }

        if (!storedToken.getUser().isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }

        return storedToken;
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable", exception);
        }
    }
}
