package com.bmisiek.todomanager.unit.areas.data.service;

import com.bmisiek.todomanager.areas.data.dto.TaskDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.data.service.TaskFetcher;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskFetcherTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskFetcher taskFetcher;

    @BeforeEach
    public void setUp() {
        taskFetcher = new TaskFetcher(taskRepository);
    }

    @Test
    public void Should_ReturnTask_WhenIdExists() {
        Long taskId = 1L;
        Long assigneeId = 1L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        Project project = new Project();
        project.setId(1L);
        
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setProject(project);
        task.setAssignee(assignee);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        TaskDto result = taskFetcher.findById(taskId);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(taskId, result.getId());
        Assertions.assertEquals("Test Task", result.getTitle());
        Assertions.assertEquals("Test Description", result.getDescription());
        Assertions.assertEquals(TaskType.TASK, result.getTaskType());
        Assertions.assertEquals(project.getId(), result.getProjectId());
        Assertions.assertEquals(assigneeId, result.getAssigneeId());
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenIdDoesNotExist() {
        Long taskId = 999L;
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskFetcher.findById(taskId));
        
        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
    }

    @Test
    public void Should_ReturnTask_WhenIdExistsAndUserIsAssignee() {
        Long taskId = 1L;
        Long assigneeId = 1L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        Project project = new Project();
        project.setId(1L);
        
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setTaskType(TaskType.TASK);
        task.setProject(project);
        task.setAssignee(assignee);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        TaskDto result = taskFetcher.findByIdForAssigneeId(taskId, assigneeId);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(taskId, result.getId());
        Assertions.assertEquals("Test Task", result.getTitle());
        Assertions.assertEquals("Test Description", result.getDescription());
        Assertions.assertEquals(TaskType.TASK, result.getTaskType());
        Assertions.assertEquals(project.getId(), result.getProjectId());
        Assertions.assertEquals(assigneeId, result.getAssigneeId());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenUserIsNotAssignee() {
        Long taskId = 1L;
        Long assigneeId = 1L;
        Long differentUserId = 2L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(assignee);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskFetcher.findByIdForAssigneeId(taskId, differentUserId));
        
        Assertions.assertEquals("User does not have access to this task", exception.getMessage());
    }

    @Test
    public void Should_ReturnListOfTasks_WhenFindingByAssigneeId() {
        Long assigneeId = 1L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        Project project = new Project();
        project.setId(1L);
        
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setTaskType(TaskType.TASK);
        task1.setProject(project);
        task1.setAssignee(assignee);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setTaskType(TaskType.BUG);
        task2.setProject(project);
        task2.setAssignee(assignee);
        
        List<Task> tasks = List.of(task1, task2);
        Mockito.when(taskRepository.findAllByAssignee_Id(assigneeId)).thenReturn(tasks);
        
        List<TaskDto> result = taskFetcher.findAllByAssigneId(assigneeId);
        
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
    public void Should_ReturnEmptyList_WhenNoTasksFoundForAssignee() {
        Long assigneeId = 999L;
        Mockito.when(taskRepository.findAllByAssignee_Id(assigneeId)).thenReturn(List.of());
        
        List<TaskDto> result = taskFetcher.findAllByAssigneId(assigneeId);
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void Should_ReturnListOfTasks_WhenFindingByProjectId() {
        Long projectId = 1L;
        
        Project project = new Project();
        project.setId(projectId);
        
        User assignee1 = new User();
        assignee1.setId(1L);
        
        User assignee2 = new User();
        assignee2.setId(2L);
        
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setTaskType(TaskType.FEATURE);
        task1.setProject(project);
        task1.setAssignee(assignee1);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setTaskType(TaskType.ISSUE);
        task2.setProject(project);
        task2.setAssignee(assignee2);
        
        List<Task> tasks = List.of(task1, task2);
        Mockito.when(taskRepository.findAllByProject_Id(projectId)).thenReturn(tasks);
        
        List<TaskDto> result = taskFetcher.findByProjectId(projectId);
        
        Assertions.assertEquals(2, result.size());
        
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals("Task 1", result.get(0).getTitle());
        Assertions.assertEquals("Description 1", result.get(0).getDescription());
        Assertions.assertEquals(TaskType.FEATURE, result.get(0).getTaskType());
        Assertions.assertEquals(assignee1.getId(), result.get(0).getAssigneeId());
        
        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals("Task 2", result.get(1).getTitle());
        Assertions.assertEquals("Description 2", result.get(1).getDescription());
        Assertions.assertEquals(TaskType.ISSUE, result.get(1).getTaskType());
        Assertions.assertEquals(assignee2.getId(), result.get(1).getAssigneeId());
    }

    @Test
    public void Should_ReturnEmptyList_WhenNoTasksFoundForProject() {
        Long projectId = 999L;
        Mockito.when(taskRepository.findAllByProject_Id(projectId)).thenReturn(List.of());
        
        List<TaskDto> result = taskFetcher.findByProjectId(projectId);
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
