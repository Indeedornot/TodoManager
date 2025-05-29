package com.bmisiek.todomanager.areas.data.dto;

import com.bmisiek.todomanager.areas.data.entity.TaskType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEditTypeDto {
    private @NotNull Long taskId;
    private @NotNull TaskType newType;
}
