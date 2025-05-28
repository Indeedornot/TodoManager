package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFetcher {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    public ProjectFetcher(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
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
        return taskRepository.findAllByAssignee_Id(assigneeId)
                .stream()
                .map(Task::getProject)
                .distinct()
                .map(ProjectDto::new)
                .distinct().toList();
    }

    public ProjectDto findByIdForAssignee(Long id, Long assigneeId) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        var userIsAssignedToProject = taskRepository.findAllByAssignee_IdAndProject_Id(assigneeId, id)
                .stream()
                .anyMatch(t -> t.getProject().getId().equals(id));
        if (!userIsAssignedToProject) {
            throw new AccessDeniedException("User is not assigned to this project");
        }

        return new ProjectDto(project);
    }
}
