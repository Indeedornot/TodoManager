package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.service.TaskDetailsService;
import com.bmisiek.todomanager.areas.data.service.TaskStatusManager;
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

@RestController
@RequiresJwt
@Tag(name = "Admin Task Details", description = "API for managing task details for administrators")
public class TaskDetailsController {
    private final TaskDetailsService taskDetailsService;
    private final TaskStatusManager taskStatusManager;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskDetailsController(TaskDetailsService taskDetailsService, TaskStatusManager taskStatusManager, UserJwtAuthenticator userAuthenticator) {
        this.taskDetailsService = taskDetailsService;
        this.taskStatusManager = taskStatusManager;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Change task assignee", description = "Updates the assignee of a task in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task assignee changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data, task ID mismatch, or task not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to change this task's assignee")
    })
    @PutMapping("/api/admin/tasks/{id}/assignee")
    public ResponseEntity<String> changeAssignee(@RequestBody TaskEditAssigneeDto taskDto, @PathVariable Long id) {
        if (taskDto.getTaskId() == null || !taskDto.getTaskId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskDetailsService.changeAssignee(taskDto, user);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "Change task type", description = "Updates the type of a task in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task type changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data, task ID mismatch, or task not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to change this task's type")
    })
    @PutMapping("/api/admin/tasks/{id}/type")
    public ResponseEntity<String> changeType(@RequestBody TaskEditTypeDto taskDto, @PathVariable Long id) {
        if (taskDto.getTaskId() == null || !taskDto.getTaskId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskDetailsService.changeTaskType(taskDto, user);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "Mark task as completed", description = "Marks a task as completed in a project owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task marked as completed successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PostMapping("/api/admin/tasks/{id}/completed")
    public ResponseEntity<String> markAsCompleted(@PathVariable Long id) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskStatusManager.markAsCompleted(id, user);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
