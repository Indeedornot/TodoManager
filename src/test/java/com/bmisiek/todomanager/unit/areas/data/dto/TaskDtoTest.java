package com.bmisiek.todomanager.unit.areas.data.dto;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.dto.info.TaskInfo;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class TaskDtoTest {
    @Test
    public void Should_Map_TaskType_To_Info() {
         TaskDto taskDto = new TaskDto();
         taskDto.setTaskType(TaskType.BUG);
         TaskInfo info = taskDto.getTaskInfo();

         Assertions.assertEquals("Bug", info.getType());
    }

    @Test
    public void Should_ReturnCorrectType_WhenGetTypeCalled() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskType(TaskType.FEATURE);
        TaskInfo info = taskDto.getTaskInfo();

        Assertions.assertEquals("Feature", info.getType());
    }


    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void Should_SerializeAndDeserializeCorrectly() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskType(TaskType.BUG);
        taskDto.setTitle("Team Meeting");
        taskDto.setDescription("Discuss project updates");

        String json = objectMapper.writeValueAsString(taskDto);
        Assertions.assertTrue(json.contains("\"taskInfo\":{\"type\":\"Bug\"}}"), "JSON should contain taskInfo type, but it does not: " + json);

        TaskDto deserializedTaskDto = objectMapper.readValue(json, TaskDto.class);

        Assertions.assertEquals(taskDto.getTaskType(), deserializedTaskDto.getTaskType());
        Assertions.assertEquals(taskDto.getTitle(), deserializedTaskDto.getTitle());
        Assertions.assertEquals(taskDto.getDescription(), deserializedTaskDto.getDescription());
        Assertions.assertEquals("Bug", deserializedTaskDto.getTaskInfo().getType());
    }
}
