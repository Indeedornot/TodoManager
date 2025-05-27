package com.bmisiek.todomanager.integration.utilities;

import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import org.springframework.stereotype.Service;

@Service
public class TestUserHandler {
    private final UserCreator userCreator;
    private final UserJwtAuthenticator authenticator;
    private final UserRepository userRepository;

    private final String USERNAME_PREFIX = "test";
    private final String DEFAULT_EMAIL_DOMAIN = "@example.com";
    private final String DEFAULT_PASSWORD = "test123";

    public TestUserHandler(UserCreator userCreator, UserJwtAuthenticator authenticator, UserRepository userRepository) {
        this.userCreator = userCreator;
        this.authenticator = authenticator;
        this.userRepository = userRepository;
    }

    private String GetUsername(Long counter) {
        return USERNAME_PREFIX + counter;
    }

    private String GetEmail(Long counter) {
        return USERNAME_PREFIX + counter + DEFAULT_EMAIL_DOMAIN;
    }

    public String createAdminAndGetToken(Long counter) {
        var username = GetUsername(counter);
        var email = GetEmail(counter);
        var password = DEFAULT_PASSWORD;

        var signUpDto = new SignUpDto(username, username, email, password, null);
        userCreator.create(signUpDto, RoleEnum.ROLE_ADMIN);

        var loginDto = new LoginDto(username, password);
        return authenticator.authenticate(loginDto);
    }

    public User getUser(Long counter) {
        var username = GetUsername(counter);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
