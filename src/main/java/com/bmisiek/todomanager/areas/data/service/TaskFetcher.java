package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public List<TaskDto> findByAssigneId(Long assigneeId) {
        return taskRepository.findAllByAssignee_Id(assigneeId)
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }
}
