package com.bmisiek.todomanager.areas.security.controller;

import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "Authentication API")
public class SignInController {

    private final UserJwtAuthenticator authenticator;

    public SignInController(UserJwtAuthenticator userAuthenticator) {
        this.authenticator = userAuthenticator;
    }

    @PostMapping("/api/security/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        try {
            final var token = authenticator.authenticate(loginDto);
            return ResponseEntity.ok(token);
        }
        catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/api/security/logout")
    public ResponseEntity<String> logoutUser() {
        try {
            authenticator.logout();
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User logged out successfully.", HttpStatus.OK);
    }
}