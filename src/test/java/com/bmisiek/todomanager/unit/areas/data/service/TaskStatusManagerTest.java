package com.bmisiek.todomanager.unit.areas.data.service;

import com.bmisiek.libraries.datetime.IDateTimeProvider;
import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.data.service.TaskStatusManager;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskStatusManagerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private IDateTimeProvider timeProvider;

    private TaskStatusManager taskStatusManager;

    @BeforeEach
    public void setUp() {
        taskStatusManager = new TaskStatusManager(taskRepository, timeProvider);
    }

    @Test
    public void Should_MarkTaskAsCompleted_WhenUserIsProjectOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Date now = new Date();

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
        task.setFinishedAt(null);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(timeProvider.now()).thenReturn(now);

        taskStatusManager.markAsCompleted(taskId, owner);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals(now, savedTask.getFinishedAt());
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenMarkingCompletedForNonExistentTask() {
        Long taskId = 999L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskStatusManager.markAsCompleted(taskId, owner));

        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenMarkingCompletedAndNotOwner() {
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
                () -> taskStatusManager.markAsCompleted(taskId, differentUser));

        Assertions.assertEquals("User cannot mark this task as completed", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }
    
    @Test
    public void Should_MarkUserTaskAsCompleted_WhenUserIsAssignee() {
        Long taskId = 1L;
        Long assigneeId = 2L;
        Date now = new Date();

        User assignee = new User();
        assignee.setId(assigneeId);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setAssignee(assignee);
        task.setFinishedAt(null);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(timeProvider.now()).thenReturn(now);

        taskStatusManager.markUserTaskAsCompleted(taskId, assignee);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals(now, savedTask.getFinishedAt());
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenMarkingUserTaskCompletedForNonExistentTask() {
        Long taskId = 999L;
        Long assigneeId = 2L;

        User assignee = new User();
        assignee.setId(assigneeId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskStatusManager.markUserTaskAsCompleted(taskId, assignee));

        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenMarkingUserTaskCompletedAndNotAssignee() {
        Long taskId = 1L;
        Long assigneeId = 2L;
        Long differentUserId = 3L;

        User assignee = new User();
        assignee.setId(assigneeId);

        User differentUser = new User();
        differentUser.setId(differentUserId);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setAssignee(assignee);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskStatusManager.markUserTaskAsCompleted(taskId, differentUser));

        Assertions.assertEquals("User cannot mark this task as completed", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    public void Should_ReturnPendingTasks_WhenProjectHasPendingTasks() {
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);

        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setTaskType(TaskType.TASK);
        task1.setProject(project);
        task1.setFinishedAt(null);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setTaskType(TaskType.BUG);
        task2.setProject(project);
        task2.setFinishedAt(null);

        List<Task> pendingTasks = List.of(task1, task2);

        Mockito.when(taskRepository.findAllByFinishedAtIsNullAndProjectId(projectId)).thenReturn(pendingTasks);

        List<TaskDto> result = taskStatusManager.getPendingTasks(projectId);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals("Task 1", result.get(0).getTitle());
        Assertions.assertEquals("Description 1", result.get(0).getDescription());
        Assertions.assertEquals(TaskType.TASK, result.get(0).getTaskType());

        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals("Task 2", result.get(1).getTitle());
        Assertions.assertEquals("Description 2", result.get(1).getDescription());
        Assertions.assertEquals(TaskType.BUG, result.get(1).getTaskType());
    }

    @Test
    public void Should_ReturnEmptyList_WhenNoTasksArePending() {
        Long projectId = 1L;
        List<Task> emptyList = List.of();

        Mockito.when(taskRepository.findAllByFinishedAtIsNullAndProjectId(projectId)).thenReturn(emptyList);

        List<TaskDto> result = taskStatusManager.getPendingTasks(projectId);

        Assertions.assertTrue(result.isEmpty());
    }
}
