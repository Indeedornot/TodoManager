package com.bmisiek.todomanager.security.controller;

import com.bmisiek.todomanager.controller.Routes;
import com.bmisiek.todomanager.security.dto.LoginDto;
import com.bmisiek.todomanager.security.service.UserAuthenticator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "Authentication API")
public class SignInController {

    private final UserAuthenticator authenticator;

    public SignInController(UserAuthenticator userAuthenticator) {
        this.authenticator = userAuthenticator;
    }

    @PostMapping(Routes.LOGIN)
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        try {
            authenticator.authenticate(loginDto);
        }
        catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    }

    @PostMapping(Routes.LOGOUT)
    public ResponseEntity<String> logoutUser() {
        try {
            authenticator.logout();
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User logged out successfully.", HttpStatus.OK);
    }
}