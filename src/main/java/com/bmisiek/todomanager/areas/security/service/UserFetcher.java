package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.dto.UserDto;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFetcher {
    private final UserRepository userRepository;
    public UserFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }
}
