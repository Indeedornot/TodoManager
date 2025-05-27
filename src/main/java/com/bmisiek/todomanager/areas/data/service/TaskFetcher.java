package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskFetcher {
    private final TaskRepository taskRepository;
    public TaskFetcher(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDto findById(Long id) throws IllegalArgumentException {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return new TaskDto(task);
    }

    public TaskDto findByIdForAssigneeId(Long id, Long assigneeId) throws IllegalArgumentException {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        if (task.getAssignee().getId() != assigneeId) {
            throw new AccessDeniedException("User does not have access to this task");
        }

        return new TaskDto(task);
    }

    public List<TaskDto> findByAssigneId(Long assigneeId) {
        return taskRepository.findAllByAssignee_Id(assigneeId)
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }
}
