package com.bmisiek.todomanager.integration.areas.admin.controller;


import com.bmisiek.libraries.mockmvc.MyRequestBuilders;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectCreateDto;
import com.bmisiek.todomanager.areas.admin.dto.project.ProjectEditDto;
import com.bmisiek.todomanager.integration.config.IntegrationTest;
import com.bmisiek.todomanager.integration.utilities.TestEntityHandler;
import com.bmisiek.todomanager.integration.utilities.TestUserHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
public class ProjectActionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserHandler testUserHandler;

    @Autowired
    private TestEntityHandler testEntityHandler;

    @Test
    public void Should_ProtectEndpointsWithJwt() throws Exception {
        var requests = new MockHttpServletRequestBuilder[] {
                MyRequestBuilders.post("/api/admin/projects"),
                MyRequestBuilders.put("/api/admin/projects/1"),
                MyRequestBuilders.delete("/api/admin/projects/1")
        };

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Test
    public void Should_CreateProject_WhenAuthenticated() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), token);

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(projectId));

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(projectId));
    }

    @Test
    public void Should_NotCreateProject_WhenInvalidData() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        var invalidDtos = new ProjectCreateDto[] {
                new ProjectCreateDto("", ""),
                new ProjectCreateDto("test", ""),
                new ProjectCreateDto("", "test")
        };

        for (var invalidDto : invalidDtos) {
            mockMvc.perform(MyRequestBuilders.postJson("/api/admin/projects", invalidDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_EditProject() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), token);

        var editDto = new ProjectEditDto(projectId, "updated1", "updated2");
        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/projects/" + projectId, editDto, token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MyRequestBuilders.getAuthed("/api/admin/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("updated1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("updated2"));
    }

    @Test
    public void Should_NotEditProject_WhenIdMismatch() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), token);

        var editDto = new ProjectEditDto(projectId, "updated1", "updated2");
        editDto.setId(projectId + 1);

        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/projects/" + projectId, editDto, token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void Should_NotEditProject_WhenDoesNotExist() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        var editDto = new ProjectEditDto(999L, "updated1", "updated2");

        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/projects/999", editDto, token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void Should_NotEdit_WhenNotOwner() throws Exception {
        var ownerToken = testUserHandler.createAdminAndGetToken(1L);
        var otherUserToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), ownerToken);

        var editDto = new ProjectEditDto(projectId, "updated1", "updated2");
        mockMvc.perform(MyRequestBuilders.putJson("/api/admin/projects/" + projectId, editDto, otherUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void Should_NotEdit_WhenInvalidData() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), token);

        var invalidEditDtos =  new ProjectEditDto[] {
                new ProjectEditDto(projectId, "", ""),
                new ProjectEditDto(projectId, "test", ""),
                new ProjectEditDto(projectId, "", "test")
        };

        for (var invalidEditDto : invalidEditDtos) {
            mockMvc.perform(MyRequestBuilders.putJson("/api/admin/projects/" + projectId, invalidEditDto, token))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    public void Should_NotDelete_WhenNotOwner() throws Exception {
        var ownerToken = testUserHandler.createAdminAndGetToken(1L);
        var otherUserToken = testUserHandler.createAdminAndGetToken(2L);

        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), ownerToken);

        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/projects/" + projectId, otherUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void Should_DeleteProject() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        Long projectId = testEntityHandler.createProject(new ProjectCreateDto("test", "test"), token);
        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/projects/" + projectId, token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void Should_NotDeleteProject_WhenDoesNotExist() throws Exception {
        String token = testUserHandler.createAdminAndGetToken(1L);
        mockMvc.perform(MyRequestBuilders.deleteAuthed("/api/admin/projects/999", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
