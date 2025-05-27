package com.bmisiek.todomanager.areas.admin.service.task;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
public class TaskCreator {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ValidatorInterface validator;

    public TaskCreator(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository, ValidatorInterface validator) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    private void validate(TaskCreateDto dto) throws IllegalArgumentException {
        validator.assertValid(dto);
    }

    public Long create(TaskCreateDto dto, User user) throws IllegalArgumentException, AccessDeniedException {
        validate(dto);

        var task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());

        var assignee = userRepository.findById(dto.getAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found with id: " + dto.getAssigneeId()));
        task.setAssignee(assignee);

        var project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + dto.getProjectId()));
        if (project.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("User does not own this project");
        }

        task.setProject(project);
        task.setTaskType(dto.getTaskType());

        taskRepository.save(task);
        return task.getId();
    }
}
