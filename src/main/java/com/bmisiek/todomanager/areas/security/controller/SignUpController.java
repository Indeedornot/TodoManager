package com.bmisiek.todomanager.areas.security.controller;

import com.bmisiek.todomanager.config.Routes;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "Authentication API")
public class SignUpController {

    private final UserCreator userCreator;

    public SignUpController(UserCreator userCreator) {
        this.userCreator = userCreator;
    }

    @PostMapping(Routes.SIGN_UP)
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        try {
            userCreator.createUser(signUpDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }
}