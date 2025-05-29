package com.bmisiek.todomanager.unit.areas.data.service;

import com.bmisiek.libraries.validation.ValidatorInterface;
import com.bmisiek.todomanager.areas.data.dto.TaskEditAssigneeDto;
import com.bmisiek.todomanager.areas.data.dto.TaskEditTypeDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.entity.Task;
import com.bmisiek.todomanager.areas.data.entity.TaskType;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.data.service.TaskDetailsService;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskDetailsServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidatorInterface validator;

    private TaskDetailsService taskDetailsService;

    @BeforeEach
    public void setUp() {
        taskDetailsService = new TaskDetailsService(taskRepository, userRepository, validator);
    }

    @Test
    public void Should_ChangeAssignee_WhenUserIsOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Long oldAssigneeId = 2L;
        Long newAssigneeId = 3L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        User oldAssignee = new User();
        oldAssignee.setId(oldAssigneeId);
        
        User newAssignee = new User();
        newAssignee.setId(newAssigneeId);
        
        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setAssignee(oldAssignee);
        
        TaskEditAssigneeDto dto = new TaskEditAssigneeDto();
        dto.setTaskId(taskId);
        dto.setNewAssigneeId(newAssigneeId);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(newAssigneeId)).thenReturn(Optional.of(newAssignee));
        
        taskDetailsService.changeAssignee(dto, owner);
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(taskRepository).save(taskCaptor.capture());
        
        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals(newAssignee, savedTask.getAssignee());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenChangingAssigneeAndNotOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Long newAssigneeId = 3L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        User otherUser = new User();
        otherUser.setId(otherUserId);
        
        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        
        TaskEditAssigneeDto dto = new TaskEditAssigneeDto();
        dto.setTaskId(taskId);
        dto.setNewAssigneeId(newAssigneeId);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskDetailsService.changeAssignee(dto, otherUser));
        
        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenChangingAssigneeForNonExistentTask() {
        Long taskId = 999L;
        Long ownerId = 1L;
        Long newAssigneeId = 2L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        TaskEditAssigneeDto dto = new TaskEditAssigneeDto();
        dto.setTaskId(taskId);
        dto.setNewAssigneeId(newAssigneeId);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> taskDetailsService.changeAssignee(dto, owner));
        
        Assertions.assertEquals("Task not found with id: " + taskId, exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    public void Should_ThrowIllegalArgumentException_WhenNewAssigneeDoesNotExist() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Long newAssigneeId = 999L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        
        TaskEditAssigneeDto dto = new TaskEditAssigneeDto();
        dto.setTaskId(taskId);
        dto.setNewAssigneeId(newAssigneeId);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(newAssigneeId)).thenReturn(Optional.empty());
        
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskDetailsService.changeAssignee(dto, owner));
        
        Assertions.assertEquals("User not found with id: " + newAssigneeId, exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    public void Should_ChangeTaskType_WhenUserIsOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setTaskType(TaskType.TASK);
        
        TaskEditTypeDto dto = new TaskEditTypeDto();
        dto.setTaskId(taskId);
        dto.setNewType(TaskType.BUG);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        taskDetailsService.changeTaskType(dto, owner);
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(taskRepository).save(taskCaptor.capture());
        
        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals(TaskType.BUG, savedTask.getTaskType());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenChangingTypeAndNotOwner() {
        Long taskId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;
        
        User owner = new User();
        owner.setId(ownerId);
        
        User otherUser = new User();
        otherUser.setId(otherUserId);
        
        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setTaskType(TaskType.TASK);
        
        TaskEditTypeDto dto = new TaskEditTypeDto();
        dto.setTaskId(taskId);
        dto.setNewType(TaskType.BUG);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskDetailsService.changeTaskType(dto, otherUser));
        
        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    public void Should_ChangeUserTaskType_WhenUserIsAssignee() {
        Long taskId = 1L;
        Long assigneeId = 2L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        Task task = new Task();
        task.setId(taskId);
        task.setTaskType(TaskType.TASK);
        task.setAssignee(assignee);
        
        TaskEditTypeDto dto = new TaskEditTypeDto();
        dto.setTaskId(taskId);
        dto.setNewType(TaskType.BUG);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        taskDetailsService.changeUserTaskType(dto, assignee);
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(taskRepository).save(taskCaptor.capture());
        
        Task savedTask = taskCaptor.getValue();
        Assertions.assertEquals(TaskType.BUG, savedTask.getTaskType());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenChangingUserTaskTypeAndNotAssignee() {
        Long taskId = 1L;
        Long assigneeId = 2L;
        Long otherUserId = 3L;
        
        User assignee = new User();
        assignee.setId(assigneeId);
        
        User otherUser = new User();
        otherUser.setId(otherUserId);
        
        Task task = new Task();
        task.setId(taskId);
        task.setTaskType(TaskType.TASK);
        task.setAssignee(assignee);
        
        TaskEditTypeDto dto = new TaskEditTypeDto();
        dto.setTaskId(taskId);
        dto.setNewType(TaskType.BUG);
        
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> taskDetailsService.changeUserTaskType(dto, otherUser));
        
        Assertions.assertEquals("User cannot edit this project", exception.getMessage());
        Mockito.verify(taskRepository, Mockito.never()).save(ArgumentMatchers.any(Task.class));
    }
}
