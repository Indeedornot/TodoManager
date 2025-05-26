package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.SecurityProperties;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserCreator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final SecurityProperties securityProperties;

    public UserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService, SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.securityProperties = securityProperties;
    }

    public Long createUser(SignUpDto signUpDto) {
        validateSignUpData(signUpDto);

        User user = mapToUser(signUpDto);

        RoleEnum roleEnum = getRole(signUpDto);
        assignRole(user, roleEnum);

        userRepository.save(user);
        return user.getId();
    }

    public Long createUser(SignUpDto signUpDto, RoleEnum roleEnum) {
        validateSignUpData(signUpDto);

        User user = mapToUser(signUpDto);
        assignRole(user, roleEnum);

        userRepository.save(user);
        return user.getId();
    }

    private void validateSignUpData(SignUpDto signUpDto) {
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            throw new IllegalArgumentException("Username is already taken!");
        }

        if(userRepository.existsByEmail(signUpDto.getEmail())){
            throw new IllegalArgumentException("Email is already taken!");
        }
    }

    private RoleEnum getRole(SignUpDto signUpDto) {
        return Optional.ofNullable(signUpDto.getPassKey())
                .filter(passKey -> passKey.equals(securityProperties.getPassKey()))
                .map(_ -> RoleEnum.ROLE_ADMIN).orElse(RoleEnum.ROLE_USER);
    }

    private void assignRole(User user, RoleEnum roleEnum) {
        Role role = roleService.findByEnum(roleEnum).orElseThrow();
        user.setRoles(Collections.singleton(role));
    }

    private User mapToUser(SignUpDto signUpDto) {
        User user = signUpDto.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
}