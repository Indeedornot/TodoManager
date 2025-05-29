package com.bmisiek.todomanager.areas.user.controller;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.service.TaskFetcher;
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

import java.util.List;

@RestController("UserTaskController")
@RequiresJwt
@Tag(name = "User Tasks", description = "API for accessing tasks assigned to the user")
public class TaskController {
    private final TaskFetcher taskFetcher;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskController(TaskFetcher taskFetcher, UserJwtAuthenticator userAuthenticator) {
        this.taskFetcher = taskFetcher;
        this.userAuthenticator = userAuthenticator;
    }

    @Operation(summary = "Get all assigned tasks", description = "Returns a list of all tasks assigned to the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all assigned tasks")
    })
    @GetMapping("/api/user/tasks")
    public ResponseEntity<List<TaskDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = taskFetcher.findAllByAssigneId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get task by ID", description = "Returns a task that the authenticated user is assigned to by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the task"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "403", description = "User is not assigned to this task")
    })
    @GetMapping("/api/user/tasks/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            return ResponseEntity.ok(taskFetcher.findByIdForAssigneeId(id, user.getId()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
