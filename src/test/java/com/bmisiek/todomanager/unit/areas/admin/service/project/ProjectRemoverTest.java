package com.bmisiek.todomanager.unit.areas.admin.service.project;

import com.bmisiek.todomanager.areas.admin.service.project.ProjectRemover;
import com.bmisiek.todomanager.areas.data.entity.Project;
import com.bmisiek.todomanager.areas.data.repository.ProjectRepository;
import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProjectRemoverTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectRemover projectRemover;

    @BeforeEach
    public void setUp() {
        projectRemover = new ProjectRemover(projectRepository);
    }

    @Test
    public void Should_RemoveProject_WhenOwnerIsValid() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setOwner(owner);
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        projectRemover.remove(projectId, owner);
        
        Mockito.verify(projectRepository).delete(project);
    }

    @Test
    public void Should_ThrowException_WhenProjectDoesNotExist() {
        Long projectId = 999L;
        User owner = new User();
        owner.setId(1L);
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectRemover.remove(projectId, owner));
        
        Assertions.assertEquals("Project not found with id: " + projectId, exception.getMessage());
        Mockito.verify(projectRepository, Mockito.never()).delete(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowAccessDeniedException_WhenUserIsNotOwner() {
        Long projectId = 1L;
        User owner = new User();
        owner.setId(1L);
        
        User differentUser = new User();
        differentUser.setId(2L);
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setOwner(owner);
        
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class,
                () -> projectRemover.remove(projectId, differentUser));
        
        Assertions.assertEquals("User does not own this project", exception.getMessage());
        Mockito.verify(projectRepository, Mockito.never()).delete(ArgumentMatchers.any());
    }
}
