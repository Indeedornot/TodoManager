package com.bmisiek.todomanager.integration.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.SecurityProperties;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@SpringBootTest
@Transactional
public class UserCreatorTest {
    @Autowired
    private UserCreator userCreator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    public void Should_CreateUser() {
        var dto = createSignUpDto();
        userCreator.create(dto);

        var user = userRepository.findByUsername(dto.getUsername()).orElseThrow();
        var roles = user.getRoles();

        AssertUserData(user, dto);
        AssertUserRole(roles, RoleEnum.ROLE_USER);
    }

    @Test
    public void Should_CreateAdminUser_WhenPassKeyIsValid() {
        var dto = createSignUpDto();

        securityProperties.setPassKey("123456789");
        dto.setPassKey(securityProperties.getPassKey());
        userCreator.create(dto);

        var user = userRepository.findByUsername(dto.getUsername()).orElseThrow();
        var roles = user.getRoles();

        AssertUserData(user, dto);
        AssertUserRole(roles, RoleEnum.ROLE_ADMIN);
    }

    private static SignUpDto createSignUpDto() {
        var name = "testuser";
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        return new SignUpDto(name, username, email, password, null);
    }

    private static void AssertUserRole(Set<Role> roles, RoleEnum role) {
        Assertions.assertEquals(1, roles.size());
        Assertions.assertEquals(roles.stream().findFirst().map(Role::getName).orElseThrow(), role.getName());
    }

    private static void AssertUserData(User user, SignUpDto dto) {
        Assertions.assertEquals(dto.getUsername(), user.getUsername());
        Assertions.assertEquals(dto.getEmail(), user.getEmail());
        Assertions.assertEquals(dto.getName(), user.getName());
    }
}
