package com.bmisiek.todomanager.areas.admin.dto.task;

import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskType taskType;
    private Long projectId;
    private Long assigneeId;

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
