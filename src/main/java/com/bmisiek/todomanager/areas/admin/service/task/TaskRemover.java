package com.bmisiek.todomanager.areas.admin.service.task;

import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TaskRemover {
    private final TaskRepository taskRepository;
    public TaskRemover(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void remove(Long taskId, User user) throws AccessDeniedException, EntityNotFoundException {
        var task = getTask(taskId);
        validateProjectOwnership(task.getProject(), user);

        taskRepository.delete(task);
    }

    private Task getTask(Long taskId) throws EntityNotFoundException {
        var project = taskRepository.findById(taskId);
        if (project.isEmpty()) {
            throw new EntityNotFoundException("Task not found with id: " + taskId);
        }
        return project.get();
    }

    private void validateProjectOwnership(Project project, User user) throws AccessDeniedException {
        if (project.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("User does not own this project");
        }
    }
}
