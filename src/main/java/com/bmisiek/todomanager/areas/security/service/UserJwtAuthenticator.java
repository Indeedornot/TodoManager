package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.JwtUtil;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserJwtAuthenticator {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public UserJwtAuthenticator(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    private static boolean isAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !authentication.getPrincipal().equals("anonymousUser");
    }

    public String authenticate(LoginDto loginDto) {
        if(isAuthenticated()) {
            throw new IllegalStateException("User is already authenticated.");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        final var userDetails = userDetailsService.loadUserByUsername(loginDto.getUsernameOrEmail());

        return jwtUtil.generateToken(userDetails);
    }

    public void logout() {
        if(!isAuthenticated()) {
                throw new IllegalStateException("User is not authenticated.");
        }

        SecurityContextHolder.clearContext();
    }
}
