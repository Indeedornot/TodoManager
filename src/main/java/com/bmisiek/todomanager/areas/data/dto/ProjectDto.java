package com.bmisiek.todomanager.areas.data.dto;

import com.bmisiek.todomanager.areas.data.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    @NotNull private Long id;
    @NotBlank private String name;
    @NotBlank private String description;
    @NotNull private Long ownerId;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.ownerId = project.getOwner().getId();
    }
}
