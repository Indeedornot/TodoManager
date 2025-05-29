package com.bmisiek.todomanager.areas.admin.controller.project;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.service.ProjectFetcher;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
@Tag(name = "Admin Projects", description = "API for managing projects for administrators")
public class ProjectController {
    private final ProjectFetcher projectFetcher;
    private final UserJwtAuthenticator userAuthenticator;

    public ProjectController(ProjectFetcher projectFetcher, UserJwtAuthenticator userAuthenticator) {
        this.projectFetcher = projectFetcher;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Get all projects", description = "Returns a list of all projects owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all projects")
    })
    @GetMapping("/api/admin/projects")
    public ResponseEntity<List<ProjectDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = projectFetcher.findAllByOwnerId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get project by ID", description = "Returns a project by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the project"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/api/admin/projects/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(projectFetcher.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
