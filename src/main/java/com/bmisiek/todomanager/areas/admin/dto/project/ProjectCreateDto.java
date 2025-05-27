package com.bmisiek.todomanager.areas.admin.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateDto {
    @NotBlank private String name;
    @NotBlank private String description;
}
