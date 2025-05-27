package com.bmisiek.todomanager.areas.admin.dto.task;

import com.bmisiek.todomanager.areas.data.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDto {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull private TaskType taskType;
    @NotNull private Long projectId;
    @NotNull private Long assigneeId;
}
