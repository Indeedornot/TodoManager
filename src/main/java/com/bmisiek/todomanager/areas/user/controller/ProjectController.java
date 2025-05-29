package com.bmisiek.todomanager.areas.user.controller;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("UserProjectController")
@RequiresJwt
@Tag(name = "User Projects", description = "API for accessing projects assigned to the user")
public class ProjectController {
    private final ProjectFetcher projectFetcher;
    private final UserJwtAuthenticator userAuthenticator;

    public ProjectController(ProjectFetcher projectFetcher, UserJwtAuthenticator userAuthenticator) {
        this.projectFetcher = projectFetcher;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Get all assigned projects", description = "Returns a list of all projects the authenticated user is assigned to")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all assigned projects")
    })
    @GetMapping("/api/user/projects")
    public ResponseEntity<List<ProjectDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = projectFetcher.findAllByAssigneeId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get project by ID", description = "Returns a project that the authenticated user is assigned to by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the project"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "403", description = "User is not assigned to this project")
    })
    @GetMapping("/api/user/projects/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            return ResponseEntity.ok(projectFetcher.findByIdForAssignee(id, user.getId()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
