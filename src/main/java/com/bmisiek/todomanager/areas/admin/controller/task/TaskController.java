package com.bmisiek.todomanager.areas.admin.controller.task;

import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskDto;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskEditDto;
import com.bmisiek.todomanager.areas.admin.service.task.TaskCreator;
import com.bmisiek.todomanager.areas.admin.service.task.TaskFetcher;
import com.bmisiek.todomanager.areas.admin.service.task.TaskEditor;
import com.bmisiek.todomanager.areas.admin.service.task.TaskRemover;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import com.bmisiek.todomanager.config.openapi.RequiresJwt;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiresJwt
public class TaskController {
    private final TaskCreator taskCreator;
    private final TaskFetcher taskFetcher;
    private final TaskEditor taskEditor;
    private final TaskRemover taskRemover;
    private final UserJwtAuthenticator userAuthenticator;

    public TaskController(TaskCreator taskCreator, TaskFetcher taskFetcher, TaskEditor taskEditor, TaskRemover taskRemover, UserJwtAuthenticator userAuthenticator) {
        this.taskCreator = taskCreator;
        this.taskFetcher = taskFetcher;
        this.taskEditor = taskEditor;
        this.taskRemover = taskRemover;
        this.userAuthenticator = userAuthenticator;
    }

    @GetMapping("/api/admin/tasks")
    public ResponseEntity<List<TaskDto>> getAll() {
        var user = userAuthenticator.getAuthenticatedUser();
        var projects = taskFetcher.findByAssigneId(user.getId());
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/api/admin/tasks")
    public ResponseEntity<Long> create(@Valid @RequestBody TaskCreateDto dto) {
        try {
            var user = userAuthenticator.getAuthenticatedUser();
            Long projectId = taskCreator.create(dto, user);
            return ResponseEntity.ok(projectId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/api/admin/tasks/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskFetcher.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
