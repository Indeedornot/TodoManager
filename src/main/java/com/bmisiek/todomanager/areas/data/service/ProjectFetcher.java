package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public List<ProjectDto> findByOwnerId(Long ownerId) {
        return projectRepository.findAllByOwnerId(ownerId).stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }
}
