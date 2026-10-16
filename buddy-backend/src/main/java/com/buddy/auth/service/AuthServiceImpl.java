package com.buddy.auth.service;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;
import com.buddy.auth.dto.RegisterRequest;
import com.buddy.auth.dto.RegisterResponse;
import com.buddy.auth.exception.DuplicateEmailException;
import com.buddy.auth.exception.InvalidCredentialsException;
import org.springframework.security.authentication.BadCredentialsException;
import com.buddy.auth.entity.Role;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.RoleRepository;
import com.buddy.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email is already registered");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHash);
        user.setEnabled(true);

        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException(
                        "Default STUDENT role not found"));

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setMessage("Registration successful");

        return response;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    @Override
    @Transactional
    public LoginResponse refresh(String rawRefreshToken) {

        var storedToken =
                refreshTokenService.validateRefreshToken(rawRefreshToken);

        User user = storedToken.getUser();

        String newRefreshToken =
                refreshTokenService.rotateRefreshToken(rawRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}


