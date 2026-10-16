package com.buddy.config;

import com.buddy.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();

            if (!token.isEmpty()
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Claims claims = jwtService.validateAndParse(token);

                    String email = claims.get("email", String.class);

                    if (email == null || email.isBlank()) {
                        throw new IllegalArgumentException(
                                "JWT email claim is missing");
                    }

                    Object roleClaim = claims.get("roles");

                    List<?> roles = roleClaim instanceof List<?> list
                            ? list
                            : List.of();

                    Collection<SimpleGrantedAuthority> authorities = roles.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(role -> new SimpleGrantedAuthority(
                                    role.startsWith("ROLE_") ? role : "ROLE_" + role))
                            .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email, null, authorities);

                    authentication.setDetails(claims.get("email"));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                } catch (JwtException | IllegalArgumentException exception) {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}