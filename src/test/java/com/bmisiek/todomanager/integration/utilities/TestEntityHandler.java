package com.bmisiek.todomanager.integration.utilities;

import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Service
public class TestEntityHandler {
    private final MockMvc mockMvc;
    public TestEntityHandler(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    public Long createProject(ProjectCreateDto projectCreateDto, String token) throws Exception {
        var result = mockMvc.perform(MyRequestBuilders.postJson("/api/admin/projects", projectCreateDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        return Long.parseLong(result.getResponse().getContentAsString());
    }

    public Long createTask(TaskCreateDto taskCreateDto, String token) throws Exception {
        var result = mockMvc.perform(MyRequestBuilders.postJson("/api/admin/tasks", taskCreateDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        return Long.parseLong(result.getResponse().getContentAsString());
    }

    public void AssertEquals(TaskDto task, Long id, TaskCreateDto dto) {
        var expectedTask = new TaskDto(
                id,
                dto.getTitle(),
                dto.getDescription(),
                dto.getTaskType(),
                dto.getProjectId(),
                dto.getAssigneeId()
        );
        Assertions.assertEquals(expectedTask, task);
    }
}
