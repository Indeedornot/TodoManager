package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.service.TaskFetcher;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
public class TaskController {
    private final TaskFetcher taskFetcher;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskController(TaskFetcher taskFetcher, UserJwtAuthenticator userAuthenticator) {
        this.taskFetcher = taskFetcher;
        this.userAuthenticator = userAuthenticator;
    }

    @GetMapping("/api/admin/tasks")
    public ResponseEntity<List<TaskDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = taskFetcher.findByAssigneId(user.getId());
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
        try {
            return ResponseEntity.ok(taskFetcher.findByAssigneId(assigneeId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
