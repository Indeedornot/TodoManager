package com.bmisiek.todomanager.unit.areas.admin.service.project;

import com.bmisiek.libraries.validation.Validator;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectEditDto;
import com.bmisiek.todomanager.areas.admin.service.project.ProjectEditor;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
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
public class ProjectEditorTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectEditor projectEditor;

    @BeforeEach
    public void setUp() {
        projectEditor = new ProjectEditor(projectRepository, new Validator());
    }

    @Test
    public void Should_EditProject_WhenValidDataAndOwner() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Original Name");
        existingProject.setDescription("Original Description");
        existingProject.setOwner(owner);
        
        ProjectEditDto editDto = new ProjectEditDto();
        editDto.setId(projectId);
        editDto.setName("Updated Name");
        editDto.setDescription("Updated Description");
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        
        projectEditor.edit(editDto, owner);
        
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.verify(projectRepository).save(projectCaptor.capture());
        
        Project savedProject = projectCaptor.getValue();
        Assertions.assertEquals("Updated Name", savedProject.getName());
        Assertions.assertEquals("Updated Description", savedProject.getDescription());
        Assertions.assertEquals(owner, savedProject.getOwner());
    }

    @Test
    public void Should_ThrowException_WhenProjectNotFound() {
        Long projectId = 999L;
        User owner = new User();
        owner.setId(1L);
        
        ProjectEditDto editDto = new ProjectEditDto();
        editDto.setId(projectId);
        editDto.setName("Updated Name");
        editDto.setDescription("Updated Description");
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectEditor.edit(editDto, owner));
        
        Assertions.assertEquals("Project not found with id: " + projectId, exception.getMessage());
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenUserIsNotOwner() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        User differentUser = new User();
        differentUser.setId(2L);
        
        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Original Name");
        existingProject.setDescription("Original Description");
        existingProject.setOwner(owner);
        
        ProjectEditDto editDto = new ProjectEditDto();
        editDto.setId(projectId);
        editDto.setName("Updated Name");
        editDto.setDescription("Updated Description");
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> projectEditor.edit(editDto, differentUser));
        
        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenNameIsEmpty() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setOwner(owner);
        
        ProjectEditDto editDto = new ProjectEditDto();
        editDto.setId(projectId);
        editDto.setName("");
        editDto.setDescription("Updated Description");
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectEditor.edit(editDto, owner));
        
        Mockito.verify(projectRepository, Mockito.never()).findById(ArgumentMatchers.any());
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenDescriptionIsEmpty() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setOwner(owner);
        
        ProjectEditDto editDto = new ProjectEditDto();
        editDto.setId(projectId);
        editDto.setName("Updated Name");
        editDto.setDescription("");
        
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectEditor.edit(editDto, owner));
        
        Mockito.verify(projectRepository, Mockito.never()).findById(ArgumentMatchers.any());
        Mockito.verify(projectRepository, Mockito.never()).save(ArgumentMatchers.any());
    }
}
