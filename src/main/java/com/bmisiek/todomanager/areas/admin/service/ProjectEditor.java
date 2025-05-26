package com.bmisiek.todomanager.areas.admin.service;

import com.bmisiek.todomanager.areas.admin.dto.ProjectEditDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProjectEditor {
    private final ProjectRepository projectRepository;
    public ProjectEditor(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void edit(ProjectEditDto projectEditDto, User user) throws IllegalArgumentException, AccessDeniedException, EntityNotFoundException {
        validateEditDto(projectEditDto);

        var project = getProject(projectEditDto);
        validateProjectOwnership(project, user);

        project.setName(projectEditDto.getName());
        project.setDescription(projectEditDto.getDescription());
        projectRepository.save(project);
    }

    private Project getProject(ProjectEditDto projectEditDto) throws IllegalArgumentException {
        var foundProject = projectRepository.findById(projectEditDto.getId());
        if (foundProject.isEmpty()) {
            throw new EntityNotFoundException("Project not found with id: " + projectEditDto.getId());
        }

        return foundProject.get();
    }

    private void validateProjectOwnership(Project project, User user) {
        if (project.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("User does not own this project");
        }
    }

    private void validateEditDto(ProjectEditDto projectEditDto) throws IllegalArgumentException {
        if (projectEditDto.getName() == null || projectEditDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (projectEditDto.getDescription() == null || projectEditDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Project description cannot be empty");
        }
    }
}
