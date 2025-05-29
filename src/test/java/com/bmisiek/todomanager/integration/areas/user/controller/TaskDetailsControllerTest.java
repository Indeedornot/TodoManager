package com.bmisiek.todomanager.integration.areas.user.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestEntityHandler;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class TaskDetailsControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityHandler testEntityHandler;

    @Autowired
    private TestUserHandler testUserHandler;

    private static String getEditTypeUrl(Long taskId) {
        return "/api/user/tasks/" + taskId + "/type";
    }

    @Test
    public void Should_ProtectEndpoint() throws Exception {
        var endpoints = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.putJson(getEditTypeUrl(1L), null),
                MyRequestBuilders.put(getMarkCompletedUrl(1L)),
        };

        for (MockHttpServletRequestBuilder endpoint : endpoints) {
            mockMvc.perform(endpoint)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Test
    public void Should_EditTaskType_WhenAssigned() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);

        var updateTypeDto = new TaskEditTypeDto(taskId, TaskType.FEATURE);

        mockMvc.perform(MyRequestBuilders.putJson(getEditTypeUrl(taskId), updateTypeDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_NotEditTaskType_WhenInvalidData() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);

        var invalidDtos = new TaskEditTypeDto[] {
                new TaskEditTypeDto(taskId, null),
                new TaskEditTypeDto(null, TaskType.FEATURE),
                new TaskEditTypeDto(999L, TaskType.FEATURE)
        };

        for (var invalidDto : invalidDtos) {
            mockMvc.perform(MyRequestBuilders.putJson(getEditTypeUrl(taskId), invalidDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_NotEditTaskType_WhenNotAssigned() throws Exception {
        var assigneeId = 1L;

        String token = testUserHandler.createAdminAndGetToken(assigneeId);
        String otherToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                assigneeId
        ), token);

        var updateTypeDto = new TaskEditTypeDto(taskId, TaskType.FEATURE);

        mockMvc.perform(MyRequestBuilders.putJson(getEditTypeUrl(taskId), updateTypeDto, otherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static String getMarkCompletedUrl(Long taskId) {
        return "/api/user/tasks/" + taskId + "/completed";
    }

    @Test
    public void Should_MarkTaskAsCompleted_WhenAssigned() throws Exception {
        var assigneeId = 1L;
        String token = testUserHandler.createAdminAndGetToken(assigneeId);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                assigneeId
        ), token);

        mockMvc.perform(MyRequestBuilders.postAuthed(getMarkCompletedUrl(taskId), token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_ReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);

        mockMvc.perform(MyRequestBuilders.postAuthed(getMarkCompletedUrl(999L), token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_ReturnForbidden_WhenNotAssigned() throws Exception {
        var assigneeId = 1L;
        String token = testUserHandler.createAdminAndGetToken(assigneeId);
        String otherToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                assigneeId
        ), token);

        mockMvc.perform(MyRequestBuilders.postAuthed(getMarkCompletedUrl(taskId), otherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
