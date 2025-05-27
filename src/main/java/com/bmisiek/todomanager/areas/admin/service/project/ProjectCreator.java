package com.bmisiek.todomanager.areas.admin.service.project;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProjectCreator {
    private final ProjectRepository projectRepository;
    private final ValidatorInterface validator;

    public ProjectCreator(ProjectRepository projectRepository, ValidatorInterface validator) {
        this.projectRepository = projectRepository;
        this.validator = validator;
    }

    private void validate(ProjectCreateDto dto) throws IllegalArgumentException {
        validator.assertValid(dto);
    }

    public Long create(ProjectCreateDto dto, User user) throws IllegalArgumentException {
        validate(dto);

        var project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setTasks(new HashSet<>());
        project.setOwner(user);

        projectRepository.save(project);
        return project.getId();
    }
}
