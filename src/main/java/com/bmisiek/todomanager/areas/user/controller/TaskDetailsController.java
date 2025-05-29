package com.bmisiek.todomanager.areas.user.controller;


import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.service.TaskDetailsService;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("UserTaskDetailsController")
@RequiresJwt
public class TaskDetailsController {
    private final TaskDetailsService taskDetailsService;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskDetailsController(TaskDetailsService taskDetailsService, UserJwtAuthenticator userAuthenticator) {
        this.taskDetailsService = taskDetailsService;
        this.userAuthenticator = userAuthenticator;
    }

    @PutMapping("/api/user/tasks/{id}/type")
    public ResponseEntity<String> changeType(@RequestBody TaskEditTypeDto taskDto, @PathVariable Long id) {
        if (taskDto.getTaskId() == null || !taskDto.getTaskId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var user = userAuthenticator.getAuthenticatedUser();
            taskDetailsService.changeUserTaskType(taskDto, user);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
