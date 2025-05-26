package com.bmisiek.todomanager.security.dto;

import com.bmisiek.todomanager.security.entity.User;
import lombok.Data;

@Data
public class SignUpDto {
    private String name;
    private String username;
    private String email;
    private String password;

    public User toUser() {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}
