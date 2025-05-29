package com.bmisiek.todomanager.areas.admin.controller.project;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.service.ProjectFetcher;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
public class ProjectController {
    private final ProjectFetcher projectFetcher;
    private final UserJwtAuthenticator userAuthenticator;

    public ProjectController(ProjectFetcher projectFetcher, UserJwtAuthenticator userAuthenticator) {
        this.projectFetcher = projectFetcher;
        this.userAuthenticator = userAuthenticator;
    }

    @GetMapping("/api/admin/projects")
    public ResponseEntity<List<ProjectDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = projectFetcher.findAllByOwnerId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/api/admin/projects/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(projectFetcher.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
