package com.bmisiek.todomanager.areas.security.dto;

import com.bmisiek.todomanager.areas.security.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    private String name;
    private String username;
    private String email;
    private String password;
    private @Nullable String passKey;

    public User toUser() {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public @Nullable String getPassKey() {
        return passKey;
    }
}
