package com.bmisiek.todomanager.areas.admin.controller.task;


import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.service.TaskDetailsService;
import com.bmisiek.todomanager.areas.data.service.TaskStatusManager;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresJwt
public class TaskDetailsController {
    private final TaskDetailsService taskDetailsService;
    private final TaskStatusManager taskStatusManager;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskDetailsController(TaskDetailsService taskDetailsService, TaskStatusManager taskStatusManager, UserJwtAuthenticator userAuthenticator) {
        this.taskDetailsService = taskDetailsService;
        this.taskStatusManager = taskStatusManager;
        this.userAuthenticator = userAuthenticator;
    }

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
