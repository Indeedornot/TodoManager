package com.bmisiek.todomanager.areas.admin.service.task.builder;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskBuilderFactory {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ValidatorInterface validator;

    public TaskBuilderFactory(ProjectRepository projectRepository, UserRepository userRepository, ValidatorInterface validator) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public TaskBuilder create() {
        return new TaskBuilder(userRepository, projectRepository, validator);
    }
}
