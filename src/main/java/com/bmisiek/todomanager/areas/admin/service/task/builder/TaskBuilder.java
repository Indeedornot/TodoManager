package com.bmisiek.todomanager.areas.admin.service.task.builder;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskBuilder {
    @NotBlank
    private String title;
    @NotBlank private String description;
    @NotNull
    private TaskType taskType;
    @NotNull private Long projectId;
    @NotNull private Long assigneeId;

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ValidatorInterface validator;

    public TaskBuilder(UserRepository userRepository, ProjectRepository projectRepository, ValidatorInterface validator) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.validator = validator;
    }

    public TaskBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder setTaskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public TaskBuilder setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public TaskBuilder setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public Task build() throws EntityNotFoundException, IllegalArgumentException {
        validator.assertValid(this);

        var task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setTaskType(taskType);

        var assigneeId = userRepository.findById(this.assigneeId)
                .orElseThrow(() -> new EntityNotFoundException("Assignee not found"));

        var project = projectRepository.findById(this.projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        task.setAssignee(assigneeId);
        task.setProject(project);
        validator.assertValid(task);

        return task;
    }
}
