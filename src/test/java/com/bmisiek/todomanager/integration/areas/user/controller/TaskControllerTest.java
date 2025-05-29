package com.bmisiek.todomanager.integration.areas.user.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
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
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserHandler testUserHandler;

    @Autowired
    private TestEntityHandler testEntityHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Should_ListTasks_OnlyForAssigned() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        testUserHandler.createAdminAndGetToken(2L);
        var otherUser = testUserHandler.getUser(2L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = testEntityHandler.createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long taskId = testEntityHandler.createTask(taskCreateDto, token);

        var otherTaskCreateDto = new TaskCreateDto("Other Task", "Other Description", TaskType.FEATURE, projectId, otherUser.getId());
        testEntityHandler.createTask(otherTaskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/tasks", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);
        var expectedTask = new TaskDto(
                taskId,
                taskCreateDto.getTitle(),
                taskCreateDto.getDescription(),
                taskCreateDto.getTaskType(),
                taskCreateDto.getProjectId(),
                taskCreateDto.getAssigneeId()
        );
        Assertions.assertEquals(1, tasks.length);
        Assertions.assertEquals(expectedTask, tasks[0]);
    }

    @Test
    public void Should_GetTaskById() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = testEntityHandler.createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long taskId = testEntityHandler.createTask(taskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var task = objectMapper.readValue(returnJson, TaskDto.class);
        testEntityHandler.AssertEquals(task, taskId, taskCreateDto);
    }

    @Test
    public void Should_NotGetTaskById_WhenNotExists() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/tasks/999", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_NotGetTaskById_WhenNotAssignedTo() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        testUserHandler.createAdminAndGetToken(2L);
        var otherUser = testUserHandler.getUser(2L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = testEntityHandler.createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, otherUser.getId());
        Long taskId = testEntityHandler.createTask(taskCreateDto, token);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/user/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
