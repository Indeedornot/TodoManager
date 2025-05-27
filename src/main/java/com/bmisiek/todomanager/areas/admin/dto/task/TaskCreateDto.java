package com.bmisiek.todomanager.areas.admin.dto.task;

import com.bmisiek.todomanager.areas.data.entity.TaskType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDto {
    private String title;
    private String description;
    private TaskType taskType;
    private Long projectId;
    private Long assigneeId;
}
