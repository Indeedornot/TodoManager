package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.service.TaskFetcher;
import com.bmisiek.todomanager.areas.data.service.TaskStatusManager;
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

@RestController
@RequiresJwt
@Tag(name = "Admin Tasks", description = "API for managing tasks for administrators")
public class TaskController {
    private final TaskFetcher taskFetcher;
    private final TaskStatusManager taskStatusManager;

    public TaskController(TaskFetcher taskFetcher, TaskStatusManager taskStatusManager) {
        this.taskFetcher = taskFetcher;
        this.taskStatusManager = taskStatusManager;
    }

    @Operation(summary = "Get all tasks by project ID", description = "Returns a list of all tasks for a specific project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks for the project")
    })
    @GetMapping("/api/admin/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getAll(@PathVariable Long projectId) {
        var projects = taskFetcher.findByProjectId(projectId);
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get task by ID", description = "Returns a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the task"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/api/admin/tasks/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskFetcher.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get tasks by assignee ID", description = "Returns all tasks assigned to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks by assignee")
    })
    @GetMapping("/api/admin/tasks/assignee/{assigneeId}")
    public ResponseEntity<List<TaskDto>> getByAssigneeId(@PathVariable Long assigneeId) {
        return ResponseEntity.ok(taskFetcher.findAllByAssigneId(assigneeId));
    }

    @Operation(summary = "Get pending tasks by project ID", description = "Returns a list of all pending tasks for a specific project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending tasks"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to this project")
    })
    @GetMapping("/api/admin/projects/{projectId}/tasks/pending")
    public ResponseEntity<List<TaskDto>> getPendingTasks(@PathVariable Long projectId) {
        try {
            var tasks = taskStatusManager.getPendingTasks(projectId);
            return ResponseEntity.ok(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
