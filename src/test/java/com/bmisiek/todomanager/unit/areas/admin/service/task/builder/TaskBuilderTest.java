package com.bmisiek.todomanager.unit.areas.admin.service.task.builder;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.service.task.builder.TaskBuilder;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class TaskBuilderTest {
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private ValidatorInterface validator;
    private TaskBuilder taskBuilder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        validator = Mockito.mock(ValidatorInterface.class);
        taskBuilder = new TaskBuilder(userRepository, projectRepository, validator);
    }

    @Test
    void build_ShouldCreateTask_WhenValidInputsProvided() {
        var user = new User();
        user.setId(1L);

        var project = new Project();
        project.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        taskBuilder.setTitle("Test Task")
                .setDescription("Test Description")
                .setTaskType(TaskType.BUG)
                .setProjectId(1L)
                .setAssigneeId(1L);

        Task task = taskBuilder.build();

        Assertions.assertNotNull(task);
        Assertions.assertEquals("Test Task", task.getTitle());
        Assertions.assertEquals("Test Description", task.getDescription());
        Assertions.assertEquals(TaskType.BUG, task.getTaskType());
        Assertions.assertEquals(user, task.getAssignee());
        Assertions.assertEquals(project, task.getProject());
    }

    @Test
    void build_ShouldThrowEntityNotFoundException_WhenAssigneeNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        taskBuilder.setTitle("Test Task")
                .setDescription("Test Description")
                .setTaskType(TaskType.BUG)
                .setProjectId(1L)
                .setAssigneeId(1L);

        Assertions.assertThrows(EntityNotFoundException.class, taskBuilder::build);
    }

    @Test
    void build_ShouldThrowEntityNotFoundException_WhenProjectNotFound() {
        var user = new User();
        user.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        taskBuilder.setTitle("Test Task")
                .setDescription("Test Description")
                .setTaskType(TaskType.BUG)
                .setProjectId(1L)
                .setAssigneeId(1L);

        Assertions.assertThrows(EntityNotFoundException.class, taskBuilder::build);
    }

    @Test
    void build_ShouldThrowIllegalArgumentException_WhenValidationFails() {
        Mockito.doThrow(new IllegalArgumentException("Validation failed")).when(validator).assertValid(Mockito.any());

        taskBuilder.setTitle("Test Task")
                .setDescription("Test Description")
                .setTaskType(TaskType.BUG)
                .setProjectId(1L)
                .setAssigneeId(1L);

        Assertions.assertThrows(IllegalArgumentException.class, taskBuilder::build);
    }
}
