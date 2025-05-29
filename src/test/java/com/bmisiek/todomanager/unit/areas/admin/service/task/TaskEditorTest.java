package com.bmisiek.todomanager.unit.areas.admin.service.task;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.admin.dto.task.TaskEditDto;
import com.bmisiek.todomanager.areas.admin.service.task.TaskEditor;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskEditorTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ValidatorInterface validator;

    private TaskEditor taskEditor;

    @BeforeEach
    public void setUp() {
        taskEditor = new TaskEditor(taskRepository, validator);
    }

    @Test
    public void Should_EditTask_WhenValidDataAndUserIsProjectOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setTaskType(TaskType.TASK);
        existingTask.setProject(project);

        TaskEditDto editDto = new TaskEditDto();
        editDto.setId(taskId);
        editDto.setTitle("Updated Title");
        editDto.setDescription("Updated Description");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        taskEditor.edit(editDto, owner);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(validator).assertValid(editDto);
        Mockito.verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals("Updated Title", savedTask.getTitle());
        Assertions.assertEquals("Updated Description", savedTask.getDescription());
        Assertions.assertEquals(TaskType.TASK, savedTask.getTaskType());
        Assertions.assertEquals(project, savedTask.getProject());
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenTaskNotFound() {
        Long taskId = 999L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        TaskEditDto editDto = new TaskEditDto();
        editDto.setId(taskId);
        editDto.setTitle("Updated Title");
        editDto.setDescription("Updated Description");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskEditor.edit(editDto, owner));

        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
        Mockito.verify(validator).assertValid(editDto);
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenUserIsNotProjectOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Long differentUserId = 2L;

        User owner = new User();
        owner.setId(ownerId);

        User differentUser = new User();
        differentUser.setId(differentUserId);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setTaskType(TaskType.TASK);
        existingTask.setProject(project);

        TaskEditDto editDto = new TaskEditDto();
        editDto.setId(taskId);
        editDto.setTitle("Updated Title");
        editDto.setDescription("Updated Description");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskEditor.edit(editDto, differentUser));

        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(validator).assertValid(editDto);
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ThrowIllegalArgumentException_WhenValidationFails() {
        Long taskId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        TaskEditDto editDto = new TaskEditDto();
        editDto.setId(taskId);
        editDto.setTitle("");
        editDto.setDescription("Updated Description");

        Mockito.doThrow(new IllegalArgumentException("Title cannot be empty"))
                .when(validator).assertValid(editDto);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskEditor.edit(editDto, owner));

        Assertions.assertEquals("Title cannot be empty", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }
}
