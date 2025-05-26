package com.bmisiek.todomanager.security.service;

import com.bmisiek.todomanager.security.dto.LoginDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticator {
    private final AuthenticationManager authenticationManager;
    public UserAuthenticator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private static boolean isAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !authentication.getPrincipal().equals("anonymousUser");
    }

    public void authenticate(LoginDto loginDto) {
        if(isAuthenticated()) {
            throw new IllegalStateException("User is already authenticated.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        var authContext = SecurityContextHolder.getContext();
        authContext.setAuthentication(authentication);
    }

    public void logout() {
        if(!isAuthenticated()) {
                throw new IllegalStateException("User is not authenticated.");
        }

        SecurityContextHolder.clearContext();
    }
}
