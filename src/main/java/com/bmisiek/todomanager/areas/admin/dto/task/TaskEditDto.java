package com.bmisiek.todomanager.areas.admin.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEditDto {
    @NotNull private Long id;
    @NotBlank private String title;
    @NotBlank private String description;
}
