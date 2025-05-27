package com.bmisiek.todomanager.areas.admin.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEditDto {
    private Long id;
    private String name;
    private String description;
}
