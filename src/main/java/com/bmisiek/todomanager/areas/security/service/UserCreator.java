package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserCreator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    public Long createUser(SignUpDto signUpDto) {
        validateSignUpData(signUpDto);

        User user = mapToUser(signUpDto);
        assignRole(user, RoleEnum.ROLE_USER);

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