package com.bmisiek.todomanager.areas.security.controller;

import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PostMapping("/api/security/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        try {
            final var token = authenticator.authenticate(loginDto);
            return ResponseEntity.ok(token);
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the current user and invalidates their token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged out successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid logout request")
    })
    @PostMapping("/api/security/logout")
    public ResponseEntity<String> logoutUser() {
        try {
            authenticator.logout();
            return ResponseEntity.ok("User logged out successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Invalid logout request.");
        }
    }
}