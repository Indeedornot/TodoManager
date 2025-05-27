package com.bmisiek.todomanager.integration.areas.admin.controller;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskEditDto;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
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
    private UserCreator userCreator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJwtAuthenticator authenticator;

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
    public void Should_ListTasks() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, user.getId());
        Long taskId = createTask(taskCreateDto, token);

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
        Assertions.assertEquals(tasks[0], expectedTask);
    }

    @Test
    public void Should_CreateTask() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, user.getId());
        Long taskId = createTask(taskCreateDto, token);

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var task = objectMapper.readValue(returnJson, TaskDto.class);
        Assertions.assertEquals(task, new TaskDto(
                taskId,
                taskCreateDto.getTitle(),
                taskCreateDto.getDescription(),
                taskCreateDto.getTaskType(),
                taskCreateDto.getProjectId(),
                taskCreateDto.getAssigneeId()
        ));
    }

    @Test
    public void Should_NotCreateTask_WhenInvalidData() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var invalidTaskCreateDtos = new TaskCreateDto[]{
                new TaskCreateDto("", "Task Description", TaskType.BUG, projectId, user.getId()),
                new TaskCreateDto("Test Task", "", TaskType.BUG, projectId, user.getId()),
                new TaskCreateDto("Test Task", "Task Description", null, projectId, user.getId()),
                new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, null, user.getId()),
                new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, null)
        };

        for (TaskCreateDto invalidTaskCreateDto : invalidTaskCreateDtos) {
            mockMvc.perform(MyRequestBuilders.postJson("/api/admin/tasks", invalidTaskCreateDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_NotCreateTask_WhenNotProjectOwner() throws Exception {
        var ownerToken = createUserAndGetToken(1L);
        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, ownerToken);

        String otherUserToken = createUserAndGetToken(2L);
        User otherUser = getUser(2L);
        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, otherUser.getId());

        mockMvc.perform(MyRequestBuilders.postJson("/api/admin/tasks", taskCreateDto, otherUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void Should_NotEditTask_WhenInvalidData() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, user.getId());
        Long taskId = createTask(taskCreateDto, token);

        var invalidEditDtos = new TaskCreateDto[]{
                new TaskCreateDto("", "Updated Description", TaskType.BUG, projectId, user.getId()),
                new TaskCreateDto("Updated Title", "", TaskType.BUG, projectId, user.getId()),
                new TaskCreateDto("Updated Title", "Updated Description", null, projectId, user.getId()),
                new TaskCreateDto("Updated Title", "Updated Description", TaskType.BUG, null, user.getId()),
                new TaskCreateDto("Updated Title", "Updated Description", TaskType.BUG, projectId, null)
        };

        for (TaskCreateDto invalidEditDto : invalidEditDtos) {
            mockMvc.perform(MyRequestBuilders.putJson("/api/admin/tasks/" + taskId, invalidEditDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_EditTask() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, user.getId());
        Long taskId = createTask(taskCreateDto, token);

        var updatedTaskDto = new TaskEditDto(taskId, "Updated Task", "Updated Description");
        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/tasks/" + taskId, updatedTaskDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var returnJson = mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        var task = objectMapper.readValue(returnJson, TaskDto.class);
        Assertions.assertEquals(task, new TaskDto(
                taskId,
                updatedTaskDto.getTitle(),
                updatedTaskDto.getDescription(),
                taskCreateDto.getTaskType(),
                projectId,
                user.getId()
        ));
    }

    @Test
    public void Should_NotEditTask_WhenNotProjectOwner() throws Exception {
        var ownerToken = createUserAndGetToken(1L);
        var otherUserToken = createUserAndGetToken(2L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, ownerToken);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, getUser(1L).getId());
        Long taskId = createTask(taskCreateDto, ownerToken);

        var updatedTaskDto = new TaskEditDto(taskId, "Updated Task", "Updated Description");
        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/tasks/" + taskId, updatedTaskDto, otherUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void Should_NotEditTask_WhenIdNotFound() throws Exception {
        String token = createUserAndGetToken(1L);
        var updatedTaskDto = new TaskEditDto(999L, "Updated Task", "Updated Description");

        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/tasks/999", updatedTaskDto, token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_DeleteTask() throws Exception {
        var token = createUserAndGetToken(1L);
        var user = getUser(1L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, token);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, user.getId());
        Long taskId = createTask(taskCreateDto, token);

        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/tasks/" + taskId, token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_NotDeleteTask_WhenNotOwner() throws Exception {
        var ownerToken = createUserAndGetToken(1L);
        var otherUserToken = createUserAndGetToken(2L);

        var projectCreateDto = new ProjectCreateDto("Test Project", "Description");
        Long projectId = createProject(projectCreateDto, ownerToken);

        var taskCreateDto = new TaskCreateDto("Test Task", "Task Description", TaskType.BUG, projectId, getUser(1L).getId());
        Long taskId = createTask(taskCreateDto, ownerToken);

        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/tasks/" + taskId, otherUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void Should_NotDeleteTask_WhenIdDoesNotExist() throws Exception {
        String token = createUserAndGetToken(1L);
        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/tasks/999", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private String createUserAndGetToken(Long counter) {
        var username = "test" + counter;
        var email = "test" + counter + "@example.com";

        var signUpDto = new SignUpDto("test", username, email, "test123", null);
        userCreator.create(signUpDto, RoleEnum.ROLE_ADMIN);

        var loginDto = new LoginDto(username, "test123");
        return authenticator.authenticate(loginDto);
    }

    private User getUser(Long counter) {
        var username = "test" + counter;
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
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
