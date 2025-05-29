package com.bmisiek.todomanager.unit.areas.admin.service.task;

import com.bmisiek.todomanager.areas.admin.service.task.TaskRemover;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskRemoverTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskRemover taskRemover;

    @BeforeEach
    public void setUp() {
        taskRemover = new TaskRemover(taskRepository);
    }

    @Test
    public void Should_RemoveTask_WhenUserIsProjectOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setProject(project);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskRemover.remove(taskId, owner);

        Mockito.verify(taskRepository).delete(task);
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenTaskNotFound() {
        Long taskId = 999L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskRemover.remove(taskId, owner));

        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).delete(Mockito.any(Task.class));
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

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setProject(project);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskRemover.remove(taskId, differentUser));

        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).delete(Mockito.any(Task.class));
    }
}
