package com.bmisiek.todomanager.integration.areas.security.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCreator userCreator;

    @Autowired
    private UserJwtAuthenticator authenticator;

    @Test
    public void Should_GetAllUsers_WhenAdmin() throws Exception {
        var token = createUserAndGetToken(1L);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/users", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("test1"));

        createUserAndGetToken(2L);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/users", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("test2"));
    }

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        mockMvc.perform(MyRequestBuilders.get("/api/admin/users"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    private String createUserAndGetToken(Long counter) {
        var username = "test" + counter;
        var email = "test" + counter + "@example.com";

        var signUpDto = new SignUpDto("test", username, email, "test123", null);
        userCreator.create(signUpDto, RoleEnum.ROLE_ADMIN);

        var loginDto = new LoginDto(username, "test123");
        return authenticator.authenticate(loginDto);
    }
}
