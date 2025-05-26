package com.bmisiek.todomanager.areas.security.controller;

import com.bmisiek.todomanager.areas.security.dto.UserDto;
import com.bmisiek.todomanager.areas.security.service.UserFetcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserFetcher userFetcher;
    public UserController(UserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    @GetMapping("/api/admin/users")
    public List<UserDto> getAllUsers() {
        return userFetcher.findAll();
    }
}
