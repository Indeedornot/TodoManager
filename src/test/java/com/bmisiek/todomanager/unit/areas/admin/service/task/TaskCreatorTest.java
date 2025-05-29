package com.bmisiek.todomanager.unit.areas.admin.service.task;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskCreateDto;
import com.bmisiek.todomanager.areas.admin.service.task.TaskCreator;
import com.bmisiek.todomanager.areas.admin.service.task.builder.TaskBuilder;
import com.bmisiek.todomanager.areas.admin.service.task.builder.TaskBuilderFactory;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class TaskCreatorTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskBuilderFactory taskBuilderFactory;

    @Mock
    private TaskBuilder taskBuilder;

    @Mock
    private ValidatorInterface validator;

    private TaskCreator taskCreator;

    @BeforeEach
    public void setUp() {
        taskCreator = new TaskCreator(taskRepository, taskBuilderFactory, validator);
    }

    @Test
    public void Should_CreateTask_WhenValidDataAndUserIsProjectOwner() {
        Long taskId = 1L;
        Long projectId = 1L;
        Long assigneeId = 2L;
        Long ownerId = 3L;

        User owner = new User();
        owner.setId(ownerId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(owner);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setProject(project);

        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Test Description");
        createDto.setTaskType(TaskType.TASK);
        createDto.setProjectId(projectId);
        createDto.setAssigneeId(assigneeId);

        Mockito.when(taskBuilderFactory.create()).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setTitle("Test Task")).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setDescription("Test Description")).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setTaskType(TaskType.TASK)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setProjectId(projectId)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setAssigneeId(assigneeId)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.build()).thenReturn(task);

        Long result = taskCreator.create(createDto, owner);

        Assertions.assertEquals(taskId, result);
        Mockito.verify(validator).assertValid(createDto);
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenUserIsNotProjectOwner() {
        Long projectId = 1L;
        Long assigneeId = 2L;
        Long ownerId = 3L;
        Long differentUserId = 4L;

        User owner = new User();
        owner.setId(ownerId);

        User differentUser = new User();
        differentUser.setId(differentUserId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(owner);

        Task task = new Task();
        task.setProject(project);

        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Test Description");
        createDto.setTaskType(TaskType.TASK);
        createDto.setProjectId(projectId);
        createDto.setAssigneeId(assigneeId);

        Mockito.when(taskBuilderFactory.create()).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setTitle("Test Task")).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setDescription("Test Description")).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setTaskType(TaskType.TASK)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setProjectId(projectId)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.setAssigneeId(assigneeId)).thenReturn(taskBuilder);
        Mockito.when(taskBuilder.build()).thenReturn(task);

        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskCreator.create(createDto, differentUser));

        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(validator).assertValid(createDto);
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ThrowIllegalArgumentException_WhenValidationFails() {
        Long projectId = 1L;
        Long assigneeId = 2L;
        Long ownerId = 3L;

        User owner = new User();
        owner.setId(ownerId);

        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("");
        createDto.setDescription("Test Description");
        createDto.setTaskType(TaskType.TASK);
        createDto.setProjectId(projectId);
        createDto.setAssigneeId(assigneeId);

        Mockito.doThrow(new IllegalArgumentException("Title cannot be empty"))
                .when(validator).assertValid(createDto);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskCreator.create(createDto, owner));

        Assertions.assertEquals("Title cannot be empty", exception.getMessage());
        Mockito.verify(taskBuilderFactory, Mockito.never()).create();
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }
}
