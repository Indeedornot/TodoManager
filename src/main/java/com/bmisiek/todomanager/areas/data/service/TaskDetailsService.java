package com.bmisiek.todomanager.areas.data.service;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TaskDetailsService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ValidatorInterface validator;

    public TaskDetailsService(TaskRepository taskRepository, UserRepository userRepository, ValidatorInterface validator) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    private boolean isOwner(Task task, User owner) {
        return task.getProject().getOwner().getId().equals(owner.getId());
    }

    private boolean isAssignee(Task task, User assignee) {
        return task.getAssignee() != null && task.getAssignee().getId().equals(assignee.getId());
    }

    private void validateCanChangeAssignee(Task task, User owner) {
        if (!isOwner(task, owner)) {
            throw new AccessDeniedException("User does not own this project");
        }
    }

    private void validateCanChangeTaskType(Task task, User owner) {
        if (!isOwner(task, owner) && !isAssignee(task, owner)) {
            throw new AccessDeniedException("User cannot edit this project");
        }
    }

    public void changeAssignee(TaskEditAssigneeDto dto, User owner) {
        validator.assertValid(dto);
        var task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + dto.getTaskId()));
        validateCanChangeAssignee(task, owner);

        var user = userRepository.findById(dto.getNewAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getNewAssigneeId()));

        task.setAssignee(user);
        taskRepository.save(task);
    }

//    public void changeTaskType(Long taskId, TaskType newType, User owner) {
//        var task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
//        validateCanChangeTaskType(task, owner);
//
//        task.setTaskType(newType);
//        taskRepository.save(task);
//    }
}
