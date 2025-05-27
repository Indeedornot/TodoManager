package com.bmisiek.todomanager.areas.admin.service.project;

import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProjectCreator {
    private final ProjectRepository projectRepository;

    public ProjectCreator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private void validate(ProjectCreateDto dto) throws IllegalArgumentException {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Project description cannot be empty");
        }
    }

    public Long create(ProjectCreateDto dto, User user) throws IllegalArgumentException {
        validate(dto);

        var project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setTasks(new HashSet<>());
        project.setOwner(user);

        projectRepository.save(project);
        return project.getId();
    }
}
