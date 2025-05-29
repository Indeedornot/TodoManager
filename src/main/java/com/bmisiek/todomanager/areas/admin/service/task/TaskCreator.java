package com.bmisiek.todomanager.areas.admin.service.task;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.admin.service.task.builder.TaskBuilderFactory;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
public class TaskCreator {
    private final TaskRepository taskRepository;
    private final TaskBuilderFactory taskBuilderFactory;
    private final ValidatorInterface validator;

    public TaskCreator(TaskRepository taskRepository, TaskBuilderFactory taskBuilderFactory, ValidatorInterface validator) {
        this.taskRepository = taskRepository;
        this.taskBuilderFactory = taskBuilderFactory;
        this.validator = validator;
    }

    private void validate(TaskCreateDto dto) throws IllegalArgumentException {
        validator.assertValid(dto);
    }

    public Long create(TaskCreateDto dto, User user) throws IllegalArgumentException, AccessDeniedException {
        validate(dto);

        var task = taskBuilderFactory.create()
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setAssigneeId(dto.getAssigneeId())
                .setProjectId(dto.getProjectId())
                .setTaskType(dto.getTaskType())
                .build();
        if (!task.getProject().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("User does not own this project");
        }

        taskRepository.save(task);
        return task.getId();
    }
}
