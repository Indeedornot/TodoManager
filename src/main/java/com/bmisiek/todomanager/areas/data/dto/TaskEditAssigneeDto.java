package com.bmisiek.todomanager.areas.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEditAssigneeDto {
    private @NotNull Long taskId;
    private @NotNull Long newAssigneeId;
}
