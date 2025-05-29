package com.bmisiek.todomanager.areas.admin.controller.project;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectEditDto;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectCreator;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectEditor;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectRemover;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController("AdminProjectActionController")
@RequiresJwt
@Tag(name = "Admin Projects Actions", description = "API for creating, updating and deleting projects")
public class ProjectActionController {
    private final ProjectCreator projectCreator;
    private final ProjectEditor projectEditor;
    private final ProjectRemover projectRemover;
    private final UserJwtAuthenticator userAuthenticator;

    public ProjectActionController(ProjectCreator projectCreator, ProjectEditor projectEditor, ProjectRemover projectRemover, UserJwtAuthenticator userAuthenticator) {
        this.projectCreator = projectCreator;
        this.projectEditor = projectEditor;
        this.projectRemover = projectRemover;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Create a new project", description = "Creates a new project for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid project data")
    })
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

    @Operation(summary = "Update a project", description = "Updates an existing project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid project data"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "403", description = "User is not authorized to edit this project")
    })
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

    @Operation(summary = "Delete a project", description = "Deletes an existing project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "403", description = "User is not authorized to delete this project")
    })
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