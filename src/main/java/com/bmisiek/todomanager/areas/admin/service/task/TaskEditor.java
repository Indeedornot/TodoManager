package com.bmisiek.todomanager.areas.admin.service.task;

import com.bmisiek.todomanager.areas.admin.dto.task.TaskEditDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TaskEditor {
    private final TaskRepository taskRepository;
    public TaskEditor(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void edit(TaskEditDto taskEditDto, User user) throws IllegalArgumentException, AccessDeniedException, EntityNotFoundException {
        validateEditDto(taskEditDto);

        var task = getTask(taskEditDto);
        validateProjectOwnership(task.getProject(), user);

        task.setTitle(taskEditDto.getTitle());
        task.setDescription(taskEditDto.getDescription());
        taskRepository.save(task);
    }

    private Task getTask(TaskEditDto taskEditDto) throws IllegalArgumentException {
        var foundProject = taskRepository.findById(taskEditDto.getId());
        if (foundProject.isEmpty()) {
            throw new EntityNotFoundException("Task not found with id: " + taskEditDto.getId());
        }

        return foundProject.get();
    }

    private void validateProjectOwnership(Project project, User user) {
        if (project.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("User does not own this project");
        }
    }

    private void validateEditDto(TaskEditDto taskEditDto) throws IllegalArgumentException {
        if (taskEditDto.getTitle() == null || taskEditDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        if (taskEditDto.getDescription() == null || taskEditDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
    }
}
