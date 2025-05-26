package com.bmisiek.todomanager.areas.admin.controller;

import com.bmisiek.todomanager.areas.admin.dto.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.ProjectDto;
import com.bmisiek.todomanager.areas.admin.dto.ProjectEditDto;
import com.bmisiek.todomanager.areas.admin.service.ProjectCreator;
import com.bmisiek.todomanager.areas.admin.service.ProjectEditor;
import com.bmisiek.todomanager.areas.admin.service.ProjectFetcher;
import com.bmisiek.todomanager.areas.admin.service.ProjectRemover;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final ProjectCreator projectCreator;
    private final ProjectFetcher projectFetcher;
    private final ProjectEditor projectEditor;
    private final ProjectRemover projectRemover;
    private final UserJwtAuthenticator userAuthenticator;

    public ProjectController(ProjectCreator projectCreator, ProjectFetcher projectFetcher, ProjectEditor projectEditor, ProjectRemover projectRemover, UserJwtAuthenticator userAuthenticator) {
        this.projectCreator = projectCreator;
        this.projectFetcher = projectFetcher;
        this.projectEditor = projectEditor;
        this.projectRemover = projectRemover;
        this.userAuthenticator = userAuthenticator;
    }

    @GetMapping("/api/admin/projects")
    public ResponseEntity<List<ProjectDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = projectFetcher.findByOwnerId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/api/admin/projects")
    public ResponseEntity<Long> create(ProjectCreateDto dto) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            Long projectId = projectCreator.create(dto, user);
            return ResponseEntity.ok(projectId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/admin/projects/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(projectFetcher.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/admin/projects/{id}")
    public ResponseEntity<String> edit(ProjectEditDto dto, @PathVariable Long id) {
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            projectEditor.edit(dto, user);
            return ResponseEntity.ok("Project updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/api/admin/projects/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            projectRemover.remove(id, user);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
