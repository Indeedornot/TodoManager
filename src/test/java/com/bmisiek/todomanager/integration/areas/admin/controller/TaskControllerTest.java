package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.get("/api/admin/tasks"),
                MyRequestBuilders.post("/api/admin/tasks"),
                MyRequestBuilders.get("/api/admin/tasks/1"),
                MyRequestBuilders.put("/api/admin/tasks/1"),
                MyRequestBuilders.delete("/api/admin/tasks/1")
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
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, currentUser.getId());
        Long taskId = createTask(taskCreateDto, token);

        var otherTaskCreateDto = new TaskCreateDto("Other Task", "Other Description", TaskType.FEATURE, projectId, otherUser.getId());
        createTask(otherTaskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks", token))
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
        Assertions.assertEquals(tasks[0], expectedTask);
    }

    private Long createProject(ProjectCreateDto projectCreateDto, String token) throws Exception {
        var result = mockMvc.perform(MyRequestBuilders.postJson("/api/admin/projects", projectCreateDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        return Long.parseLong(result.getResponse().getContentAsString());
    }

    private Long createTask(TaskCreateDto taskCreateDto, String token) throws Exception {
        var result = mockMvc.perform(MyRequestBuilders.postJson("/api/admin/tasks", taskCreateDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        return Long.parseLong(result.getResponse().getContentAsString());
    }
}
