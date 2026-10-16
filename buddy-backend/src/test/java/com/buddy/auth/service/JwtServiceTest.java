package com.buddy.auth.service;

import com.buddy.auth.entity.Role;
import com.buddy.auth.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String VALID_SECRET =
            "buddy-test-secret-key-at-least-32-bytes-long";

    private final JwtService jwtService =
            new JwtService(VALID_SECRET, 900);

    @Test
    void generateAccessTokenShouldContainExpectedClaims() {
        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        var claims = jwtService.validateAndParse(token);

        assertEquals("42", claims.getSubject());
        assertEquals("test@buddy.com", claims.get("email", String.class));
        assertEquals(
                Set.of("ROLE_STUDENT"),
                Set.copyOf(claims.get("roles", java.util.List.class))
        );
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generatedTokenShouldExpireAfterConfiguredTtl() {
        String token = jwtService.generateAccessToken(createUser());

        var claims = jwtService.validateAndParse(token);

        long lifetimeSeconds =
                (claims.getExpiration().getTime()
                        - claims.getIssuedAt().getTime()) / 1000;

        assertEquals(900, lifetimeSeconds);
        assertEquals(900, jwtService.getAccessTokenTtlSeconds());
    }

    @Test
    void tokenSignedWithDifferentKeyShouldBeRejected() {
        String token = jwtService.generateAccessToken(createUser());

        var wrongKey = Keys.hmacShaKeyFor(
                "a-completely-different-secret-key-32-bytes"
                        .getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(
                JwtException.class,
                () -> Jwts.parser()
                        .verifyWith(wrongKey)
                        .build()
                        .parseSignedClaims(token)
        );
    }

    @Test
    void secretShorterThan32BytesShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("too-short", 900)
        );
    }

    @Test
    void zeroTtlShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(VALID_SECRET, 0)
        );
    }

    @Test
    void negativeTtlShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(VALID_SECRET, -1)
        );
    }

    private User createUser() {
        Role role = new Role();
        role.setName("ROLE_STUDENT");

        User user = new User();
        user.setId(42L);
        user.setEmail("test@buddy.com");
        user.setRoles(Set.of(role));

        return user;
    }
}