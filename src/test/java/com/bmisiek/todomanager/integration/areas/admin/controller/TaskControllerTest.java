package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestEntityHandler;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.get("/api/admin/projects/1/tasks"),
                MyRequestBuilders.get("/api/admin/tasks/1"),
                MyRequestBuilders.get("/api/admin/tasks/assignee/1")
        };

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

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

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/assignee/" + currentUser.getId(), token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);
        Assertions.assertEquals(1, tasks.length);
        testEntityHandler.AssertEquals(tasks[0], taskId, taskCreateDto);
    }

    @Test
    public void Should_ListTasks_OnlyForProject() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        testUserHandler.createAdminAndGetToken(2L);
        var otherUser = testUserHandler.getUser(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long otherProjectId = testEntityHandler.createProject(new ProjectCreateDto("Other Project", "Other Description"), token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long task1Id = testEntityHandler.createTask(taskCreateDto, token);

        var otherTaskCreateDto = new TaskCreateDto("Other Task", "Other Description", TaskType.FEATURE, otherProjectId, otherUser.getId());
        Long task2Id = testEntityHandler.createTask(otherTaskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects/" + projectId + "/tasks", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);

        Assertions.assertEquals(1, tasks.length);
        testEntityHandler.AssertEquals(tasks[0], task1Id, taskCreateDto);
    }

    @Test
    public void Should_GetTask() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long taskId = testEntityHandler.createTask(taskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var task = objectMapper.readValue(returnJson, TaskDto.class);
        testEntityHandler.AssertEquals(task, taskId, taskCreateDto);
    }

    @Test
    public void Should_Not_GetTask_WhenNotFound() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var taskId = 999L;

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_ReturnPendingTasks_WhenExist() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);
        var currentUser = testUserHandler.getUser(1L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);

        var taskCreateDto = new TaskCreateDto("Pending Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long taskId = testEntityHandler.createTask(taskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed(getPendingTasksUrl(projectId), token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);
        Assertions.assertEquals(1, tasks.length);
        testEntityHandler.AssertEquals(tasks[0], taskId, taskCreateDto);
    }

    @Test
    public void Should_ReturnEmptyList_WhenNoTasks() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed(getPendingTasksUrl(projectId), token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);
        Assertions.assertEquals(0, tasks.length);
    }

    @Test
    public void Should_ReturnEmptyList_WhenNoPendingTasks() throws Exception {
        var token = testUserHandler.createAdminAndGetToken(1L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);

        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Completed Task",
                "Task Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);
        mockMvc.perform(MyRequestBuilders.postAuthed("/api/admin/tasks/" + taskId + "/completed", token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed(getPendingTasksUrl(projectId), token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tasks = objectMapper.readValue(returnJson, TaskDto[].class);
        Assertions.assertEquals(0, tasks.length);
    }


    private static String getPendingTasksUrl(Long projectId) {
        return "/api/admin/projects/" + projectId + "/tasks/pending";
    }
}
