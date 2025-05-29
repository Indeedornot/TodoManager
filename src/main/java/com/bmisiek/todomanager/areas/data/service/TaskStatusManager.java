package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TaskStatusManager {
    private final TaskRepository taskRepository;
    public TaskStatusManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void markAsCompleted(Long taskId, User user) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        if(!task.getProject().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("User cannot mark this task as completed");
        }

        setDateFinished(task);
        taskRepository.save(task);
    }

    public void markUserTaskAsCompleted(Long taskId, User user) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        if(!task.getAssignee().getId().equals(user.getId())) {
            throw new AccessDeniedException("User cannot mark this task as completed");
        }

        setDateFinished(task);
        taskRepository.save(task);
    }

    public List<TaskDto> getPendingTasks(Long projectId) {
        var tasks = taskRepository.findAllByFinishedAtIsNullAndProjectId(projectId);
        return tasks.stream()
                .map(TaskDto::new)
                .toList();
    }

    private static void setDateFinished(Task task) {
        task.setFinishedAt(Instant.now().atZone(ZoneOffset.systemDefault()));
    }
}
