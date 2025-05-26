package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.ProjectCreateDto;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCreator userCreator;

    @Autowired
    private UserJwtAuthenticator authenticator;

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.get("/api/admin/projects"),
                MyRequestBuilders.post("/api/admin/projects"),
                MyRequestBuilders.get("/api/admin/projects/1"),
                MyRequestBuilders.put("/api/admin/projects/1"),
                MyRequestBuilders.delete("/api/admin/projects/1")
        };

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Test
    public void Should_CreateProject_WhenAuthenticated() throws Exception {
        String token = createUserAndGetToken();
        Long projectId = createProject(new ProjectCreateDto("test", "test"), token);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(projectId));

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(projectId));
    }

    private String createUserAndGetToken() {
        var signUpDto = new SignUpDto("test", "test123", "test123", "test123", null);
        userCreator.createUser(signUpDto, RoleEnum.ROLE_ADMIN);

        var loginDto = new LoginDto("test123", "test123");
        return authenticator.authenticate(loginDto);
    }

    private Long createProject(ProjectCreateDto dto, String token) throws Exception {
        var id = mockMvc
                .perform(MyRequestBuilders.postJson("/api/admin/projects", dto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(id);
    }
}
