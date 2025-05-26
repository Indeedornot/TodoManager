package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.JwtUtil;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserJwtAuthenticator {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserJwtAuthenticator(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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

    public User getAuthenticatedUser() {
        if(!isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof UserDetails user) {
            return userRepository
                    .findByUsernameOrEmail(user.getUsername(), user.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in the database."));
        } else {
            throw new IllegalStateException("Authenticated principal is not a User instance.");
        }
    }
}
