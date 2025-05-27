package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFetcher {
    private final ProjectRepository projectRepository;
    public ProjectFetcher(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectDto findById(Long id) throws IllegalArgumentException {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        return new ProjectDto(project);
    }

    public List<ProjectDto> findAllByOwnerId(Long ownerId) {
        return projectRepository.findAllByOwnerId(ownerId).stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Finds all projects in which a user is assigned to at least one task.
     */
    public List<ProjectDto> findAllByAssigneeId(Long assigneeId) {
        return projectRepository.findAllByTasksAssignee_Id(assigneeId).stream()
                .map(ProjectDto::new)
                .distinct().toList();
    }

    public ProjectDto findByIdForAssignee(Long id, Long assigneeId) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        var userIsAssignedToProject = projectRepository.findAllByTasksAssignee_Id(assigneeId)
                .stream()
                .anyMatch(p -> p.getId().equals(id));
        if (!userIsAssignedToProject) {
            throw new AccessDeniedException("User is not assigned to this project");
        }

        return new ProjectDto(project);
    }
}
