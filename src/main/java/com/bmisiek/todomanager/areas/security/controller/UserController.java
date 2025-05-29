package com.bmisiek.todomanager.areas.security.controller;

import com.bmisiek.todomanager.areas.security.dto.UserDto;
import com.bmisiek.todomanager.areas.security.service.UserFetcher;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiresJwt
@Tag(name = "User Management", description = "API for managing users")
public class UserController {
    private final UserFetcher userFetcher;
    public UserController(UserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users in the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    })
    @GetMapping("/api/admin/users")
    public List<UserDto> getAllUsers() {
        return userFetcher.findAll();
    }
}
