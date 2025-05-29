package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestEntityHandler;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class TaskDetailsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityHandler testEntityHandler;

    @Autowired
    private TestUserHandler testUserHandler;

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.put(getEditAssigneeUrl(1L)),
                MyRequestBuilders.put(getEditTypeUrl(1L)),
                MyRequestBuilders.postAuthed(getMarkCompletedUrl(1L), "invalid-token")
        };

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Test
    public void Should_UpdateTaskAssignee_WhenOwner() throws Exception {
        var initialAssigneeId = 1L;
        var newAssigneeId = 2L;

        String token = testUserHandler.createAdminAndGetToken(initialAssigneeId);
        testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);

        var updateAssigneeDto = new TaskEditAssigneeDto(taskId, 2L);
        mockMvc.perform(MyRequestBuilders.putJson(getEditAssigneeUrl(taskId), updateAssigneeDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assigneeId").value(newAssigneeId));
    }

    @Test
    public void Should_NotUpdateTaskAssignee_WhenInvalidData() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);

        var invalidDtos = new TaskEditAssigneeDto[] {
                new TaskEditAssigneeDto(taskId, null),
                new TaskEditAssigneeDto(null, 2L),
                new TaskEditAssigneeDto(999L, 2L)
        };

        for (var invalidDto : invalidDtos) {

            mockMvc.perform(MyRequestBuilders.putJson(getEditAssigneeUrl(taskId), invalidDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_Not_UpdateTaskAssignee_WhenNotOwner() throws Exception {
        var initialAssigneeId = 1L;
        var newAssigneeId = 2L;

        String token = testUserHandler.createAdminAndGetToken(initialAssigneeId);
        String otherToken = testUserHandler.createAdminAndGetToken(newAssigneeId);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                initialAssigneeId
        ), token);

        var updateAssigneeDto = new TaskEditAssigneeDto(taskId, newAssigneeId);

        mockMvc.perform(MyRequestBuilders.putJson(getEditAssigneeUrl(taskId), updateAssigneeDto, otherToken))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static @NotNull String getEditAssigneeUrl(Long taskId) {
        return "/api/admin/tasks/" + taskId + "/assignee";
    }

    @Test
    public void Should_UpdateTaskType_WhenOwner() throws Exception {
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

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskType").value(TaskType.FEATURE.name()));
    }

    @Test
    public void Should_NotUpdateTaskType_WhenInvalidData() throws Exception {
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
    public void Should_Not_UpdateTaskType_WhenNotOwner() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        String otherToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                1L
        ), token);

        var updateTypeDto = new TaskEditTypeDto(taskId, TaskType.FEATURE);

        mockMvc.perform(MyRequestBuilders.putJson(getEditTypeUrl(taskId), updateTypeDto, otherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static @NotNull String getEditTypeUrl(Long taskId) {
        return "/api/admin/tasks/" + taskId + "/type";
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
    public void Should_NotMarkTaskAsCompleted_WhenTaskDoesNotExist() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);

        mockMvc.perform(MyRequestBuilders.postAuthed(getMarkCompletedUrl(999L), token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_NotMarkTaskAsCompleted_WhenNotOwner() throws Exception {
        var ownerId = 1L;
        String token = testUserHandler.createAdminAndGetToken(ownerId);
        String otherToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("Test Project", "Description"), token);
        Long taskId = testEntityHandler.createTask(new TaskCreateDto(
                "Test Task",
                "Description",
                TaskType.BUG,
                projectId,
                ownerId
        ), token);

        mockMvc.perform(MyRequestBuilders.postAuthed(getMarkCompletedUrl(taskId), otherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static String getMarkCompletedUrl(Long taskId) {
        return "/api/admin/tasks/" + taskId + "/completed";
    }
}
