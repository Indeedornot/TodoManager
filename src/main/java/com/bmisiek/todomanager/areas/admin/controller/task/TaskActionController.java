package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskEditDto;
import com.bmisiek.todomanager.areas.admin.service.task.TaskCreator;
import com.bmisiek.todomanager.areas.admin.service.task.TaskEditor;
import com.bmisiek.todomanager.areas.admin.service.task.TaskRemover;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresJwt
@Tag(name = "Admin Task Actions", description = "API for creating, updating and deleting tasks")
public class TaskActionController {
    private final TaskCreator taskCreator;
    private final TaskEditor taskEditor;
    private final TaskRemover taskRemover;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskActionController(TaskCreator taskCreator, TaskEditor taskEditor, TaskRemover taskRemover, UserJwtAuthenticator userAuthenticator) {
        this.taskCreator = taskCreator;
        this.taskEditor = taskEditor;
        this.taskRemover = taskRemover;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Create a new task", description = "Creates a new task in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data or project not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to create tasks in this project")
    })
    @PostMapping("/api/admin/tasks")
    public ResponseEntity<Long> create(@Valid @RequestBody TaskCreateDto dto) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            Long projectId = taskCreator.create(dto, user);
            return ResponseEntity.ok(projectId);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "Update a task", description = "Updates an existing task in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data or ID mismatch"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this task")
    })
    @PutMapping("/api/admin/tasks/{id}")
    public ResponseEntity<String> edit(@RequestBody TaskEditDto dto, @PathVariable Long id) {
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskEditor.edit(dto, user);
            return ResponseEntity.ok().build();
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

    @Operation(summary = "Delete a task", description = "Deletes an existing task in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this task")
    })
    @DeleteMapping("/api/admin/tasks/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskRemover.remove(id, user);
            return ResponseEntity.ok().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
