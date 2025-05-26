package com.bmisiek.todomanager.areas.admin.service;

import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectRemover {
    private final ProjectRepository projectRepository;
    public ProjectRemover(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void remove(Long projectId, User user) throws IllegalArgumentException, AccessDeniedException, EntityNotFoundException {
        var project = getProject(projectId);
        validateProjectOwnership(project, user);

        projectRepository.delete(project);
    }

    private Project getProject(Long projectId) throws IllegalArgumentException {
        var project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new EntityNotFoundException("Project not found with id: " + projectId);
        }
        return project.get();
    }

    private void validateProjectOwnership(Project project, User user) throws AccessDeniedException {
        if (project.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("User does not own this project");
        }
    }
}
