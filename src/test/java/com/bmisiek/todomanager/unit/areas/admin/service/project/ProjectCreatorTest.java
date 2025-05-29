package com.bmisiek.todomanager.unit.areas.admin.service.project;

import com.bmisiek.libraries.validation.Validator;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectCreator;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectCreatorTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectCreator projectCreator;

    @BeforeEach
    public void setUp() {
        projectCreator = new ProjectCreator(projectRepository, new Validator());
    }

    @Test
    public void Should_CreateProject_WhenValidData() {
        User mockUser = new User();
        mockUser.setId(1L);
        
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription("Test Description");
        
        Project savedProject = new Project();
        saveProjectWithId(savedProject, 1L);

        Long projectId = projectCreator.create(dto, mockUser);
        
        Assertions.assertEquals(1L, projectId);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.verify(projectRepository).save(projectCaptor.capture());
        
        Project capturedProject = projectCaptor.getValue();
        Assertions.assertEquals("Test Project", capturedProject.getName());
        Assertions.assertEquals("Test Description", capturedProject.getDescription());
        Assertions.assertEquals(mockUser, capturedProject.getOwner());
        Assertions.assertNotNull(capturedProject.getTasks());
    }

    private void saveProjectWithId(Project savedProject, Long id) {
        Mockito.when(projectRepository.save(ArgumentMatchers.any(Project.class)))
                .then(f -> {
                    var p = f.getArgument(0, Project.class);
                    p.setId(id);
                    return p;
                })
                .thenReturn(savedProject);
    }

    @Test
    public void Should_ThrowException_WhenNameIsEmpty() {
        User mockUser = new User();
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("");
        dto.setDescription("Test Description");
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectCreator.create(dto, mockUser));
        
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenDescriptionIsEmpty() {
        User mockUser = new User();
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription("");
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectCreator.create(dto, mockUser));
        
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenNameIsNull() {
        User mockUser = new User();
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName(null);
        dto.setDescription("Test Description");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectCreator.create(dto, mockUser));
        
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenDescriptionIsNull() {
        User mockUser = new User();
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription(null);
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectCreator.create(dto, mockUser));
        
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }
}
