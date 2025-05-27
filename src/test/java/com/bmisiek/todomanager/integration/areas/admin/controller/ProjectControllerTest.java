package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserHandler testUserHandler;

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.get("/api/admin/projects"),
                MyRequestBuilders.get("/api/admin/projects/1")
        };

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Test
    public void Should_NotFindProject_WhenIdDoesNotExist() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects/999", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_FindMultipleProjects() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId1 = createProject(new ProjectCreateDto("test1", "test1"), token);
        Long projectId2 = createProject(new ProjectCreateDto("test2", "test2"), token);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(projectId1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(projectId2));
    }

    private Long createProject(ProjectCreateDto dto, String token) throws Exception {
        var id = mockMvc
                .perform(MyRequestBuilders.postJson("/api/admin/projects", dto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(id);
    }
}
