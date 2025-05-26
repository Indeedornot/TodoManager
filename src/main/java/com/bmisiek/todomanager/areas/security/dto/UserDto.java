package com.bmisiek.todomanager.areas.security.dto;

import com.bmisiek.todomanager.areas.security.entity.User;
import lombok.Data;

@Data
public class UserDto {
    public Long id;
    public String username;
    public String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
