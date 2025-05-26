package com.bmisiek.todomanager.integration.security.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.controller.Routes;
import com.bmisiek.todomanager.security.dto.LoginDto;
import com.bmisiek.todomanager.security.dto.SignUpDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignInTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Should_LoginUser() throws Exception {
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        CreateUser(username, email, password);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_NotLoginUser_WhenUsernameOrEmailDoesNotExist() throws Exception {
        var username = "nonexistentuser";
        var password = "testpassword";

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void Should_NotLoginUser_WhenPasswordIsIncorrect() throws Exception {
        var username = "testuser";
        var email = "testtest";
        var password = "correctpassword";
        var wrongPassword = "wrongpassword";

        CreateUser(username, email, password);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(wrongPassword);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void Should_NotLoginUser_WhenAlreadyLoggedIn() throws Exception {
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        CreateUser(username, email, password);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        var token = mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto, token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void Should_LogoutUser() throws Exception {
        var username = "testuser";
        var email = "testtest";
        var password = "testpassword";

        CreateUser(username, email, password);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(username);
        loginDto.setPassword(password);

        var token = mockMvc.perform(MyRequestBuilders.postJson(Routes.LOGIN, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(MyRequestBuilders.post(Routes.LOGOUT, token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_NotLogoutUser_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(MyRequestBuilders.post(Routes.LOGOUT))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    private void CreateUser(String username, String email, String password) throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName(username);
        signUpDto.setUsername(username);
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);

        mockMvc.perform(MyRequestBuilders.postJson(Routes.SIGN_UP, signUpDto))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
