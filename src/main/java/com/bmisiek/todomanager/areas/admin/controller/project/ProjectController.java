package com.bmisiek.todomanager.areas.admin.controller.project;

import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectEditDto;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectCreator;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectEditor;
import com.bmisiek.todomanager.areas.data.service.ProjectFetcher;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectRemover;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
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
    public ResponseEntity<Long> create(@RequestBody ProjectCreateDto dto) {
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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/admin/projects/{id}")
    public ResponseEntity<String> edit(@RequestBody ProjectEditDto dto, @PathVariable Long id) {
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            projectEditor.edit(dto, user);
            return ResponseEntity.ok("Project updated successfully");
        }
        catch(EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (IllegalArgumentException e) {
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
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
