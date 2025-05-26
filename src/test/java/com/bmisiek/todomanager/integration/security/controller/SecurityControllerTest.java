package com.bmisiek.todomanager.integration.security.controller;

import com.bmisiek.todomanager.controller.Routes;
import com.bmisiek.todomanager.security.dto.LoginDto;
import com.bmisiek.todomanager.security.dto.SignUpDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Should_RegisterUser() throws Exception {
        var name = "testuser";
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName(name);
        signUpDto.setUsername(username);
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);

        String registerJson = objectMapper.writeValueAsString(signUpDto);
        mockMvc.perform(
                    MockMvcRequestBuilders.post(Routes.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);
        String loginJson = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(
                    MockMvcRequestBuilders.post(Routes.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk()
        );
    }
}
