package com.bmisiek.todomanager.integration.areas.user.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestEntityHandler;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@IntegrationTest
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserHandler testUserHandler;

    @Autowired
    private TestEntityHandler testEntityHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Should_ListProjects_OnlyForAssigned() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        assignUser(projectId, currentUser, token);

        testUserHandler.createAdminAndGetToken(2L);
        var otherUser = testUserHandler.getUser(2L);
        Long otherProjectId = testEntityHandler.createProject(new ProjectCreateDto("Other Project", "Other Description"), token);
        assignUser(otherProjectId, otherUser, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/projects", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var projects = objectMapper.readValue(returnJson, ProjectDto[].class);
        Assertions.assertEquals(1, projects.length);
        Assertions.assertEquals(projects[0].getId(), projectId);
    }

    private void assignUser(Long projectId, User currentUser, String token) throws Exception {
        testEntityHandler.createTask(new TaskCreateDto("Test", "Test", TaskType.BUG, projectId, currentUser.getId()), token);
    }

    @Test
    public void Should_GetProjectById() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = testEntityHandler.createProject(projectCreateDto, token);
        assignUser(projectId, currentUser, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var project = objectMapper.readValue(returnJson, ProjectDto.class);
        Assertions.assertEquals(project.getId(), projectId);
        Assertions.assertEquals(project.getName(), projectCreateDto.getName());
    }

    @Test
    public void Should_NotGetProjectById_WhenNotExists() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/projects/999", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_NotGetProjectById_WhenNotAssignedTo() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        testUserHandler.createAdminAndGetToken(2L);
        var otherUser = testUserHandler.getUser(2L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = testEntityHandler.createProject(projectCreateDto, token);
        assignUser(projectId, otherUser, token);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}