package com.bmisiek.todomanager.unit.areas.admin.service.project;

import com.bmisiek.todomanager.areas.data.dto.ProjectDto;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.data.repository.TaskRepository;
import com.bmisiek.todomanager.areas.data.service.ProjectFetcher;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProjectFetcherTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    private ProjectFetcher projectFetcher;

    @BeforeEach
    public void setUp() {
        projectFetcher = new ProjectFetcher(projectRepository, taskRepository);
    }

    @Test
    public void Should_ReturnProject_WhenIdExists() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwner(owner);
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        ProjectDto result = projectFetcher.findById(projectId);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(projectId, result.getId());
        Assertions.assertEquals("Test Project", result.getName());
        Assertions.assertEquals("Test Description", result.getDescription());
    }

    @Test
    public void Should_ThrowEntityNotFoundException_WhenIdDoesNotExist() {
        Long projectId = 999L;
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectFetcher.findById(projectId));
        
        Assertions.assertEquals("Project not found with id: " + projectId, exception.getMessage());
    }

    @Test
    public void Should_ReturnListOfProjects_WhenFindingByOwnerId() {
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setOwner(owner);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setOwner(owner);

        List<Project> projects = List.of(project1, project2);
        Mockito.when(projectRepository.findAllByOwnerId(ownerId)).thenReturn(projects);
        
        List<ProjectDto> result = projectFetcher.findAllByOwnerId(ownerId);
        
        Assertions.assertEquals(2, result.size());
        
        Assertions.assertEquals(1L, result.getFirst().getId());
        Assertions.assertEquals("Project 1", result.get(0).getName());
        Assertions.assertEquals("Description 1", result.get(0).getDescription());
        
        Assertions.assertEquals(2L, result.get(1).getId());
        Assertions.assertEquals("Project 2", result.get(1).getName());
        Assertions.assertEquals("Description 2", result.get(1).getDescription());
    }

    @Test
    public void Should_ReturnEmptyList_WhenNoProjectsFoundForOwner() {
        Long ownerId = 999L;
        Mockito.when(projectRepository.findAllByOwnerId(ownerId)).thenReturn(List.of());
        
        List<ProjectDto> result = projectFetcher.findAllByOwnerId(ownerId);
        
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
