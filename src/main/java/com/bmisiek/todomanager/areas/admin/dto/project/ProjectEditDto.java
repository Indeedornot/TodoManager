package com.bmisiek.todomanager.areas.admin.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEditDto {
    @NotNull private Long id;
    @NotBlank private String name;
    @NotBlank private String description;
}
