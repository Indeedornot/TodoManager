package com.bmisiek.todomanager.integration.areas.security.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.config.Routes;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class SignUpTest {
    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isOk());

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void Should_NotRegisterUser_WhenUsernameAlreadyExists() throws Exception {
        var name = "testuser";
        var username = "testuser";
        var email = "testtest";
        var otherEmail = "testtest2";
        var password = "testpassword";

        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName(name);
        signUpDto.setUsername(username);
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isOk());

        signUpDto.setEmail(otherEmail);
        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void Should_NotRegisterUser_WhenEmailAlreadyExists() throws Exception {
        var name = "testuser";
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName(name);
        signUpDto.setUsername(username);
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isOk());

        signUpDto.setUsername("newusername");
        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
