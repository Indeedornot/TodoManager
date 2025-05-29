package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.service.TaskFetcher;
import com.bmisiek.todomanager.areas.data.service.TaskStatusManager;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
public class TaskController {
    private final TaskFetcher taskFetcher;
    private final TaskStatusManager taskStatusManager;

    public TaskController(TaskFetcher taskFetcher, TaskStatusManager taskStatusManager) {
        this.taskFetcher = taskFetcher;
        this.taskStatusManager = taskStatusManager;
    }

    @GetMapping("/api/admin/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getAll(@PathVariable Long projectId) {
        var projects = taskFetcher.findByProjectId(projectId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/api/admin/tasks/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskFetcher.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/admin/tasks/assignee/{assigneeId}")
    public ResponseEntity<List<TaskDto>> getByAssigneeId(@PathVariable Long assigneeId) {
        return ResponseEntity.ok(taskFetcher.findAllByAssigneId(assigneeId));
    }

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
