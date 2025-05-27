package com.bmisiek.todomanager.areas.admin.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEditDto {
    private Long id;
    private String title;
    private String description;
//    private String taskType;
//    private Long projectId;
//    private Long assigneeId;
}
