package com.bmisiek.todomanager.areas.data.dto;

import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    @NotNull private Long id;
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull private TaskType taskType;
    @NotNull private Long projectId;
    @NotNull private Long assigneeId;

    public TaskDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.taskType = task.getTaskType();
        if (task.getProject() != null) {
            this.projectId = task.getProject().getId();
        }
        if (task.getAssignee() != null) {
            this.assigneeId = task.getAssignee().getId();
        }
    }
}
