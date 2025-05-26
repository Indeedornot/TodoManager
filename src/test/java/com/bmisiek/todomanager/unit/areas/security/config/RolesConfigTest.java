package com.bmisiek.todomanager.unit.areas.security.config;

import com.bmisiek.todomanager.areas.security.config.RolesConfig;
import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RolesConfigTest {
    private RolesConfig rolesConfig;
    private RoleRepository mockRoleRepository;

    @BeforeEach
    public void setUp() {
        mockRoleRepository = Mockito.mock(RoleRepository.class);
        rolesConfig = new RolesConfig(mockRoleRepository);
    }

    private <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> collection = new ArrayList<>();
        iterable.forEach(collection::add);
        return collection;
    }

    @Test
    public void Should_AddRoles() {
        Mockito.when(mockRoleRepository.findAll()).thenReturn(List.of());
        rolesConfig.run();
        Mockito.verify(mockRoleRepository).saveAll(Mockito.argThat(roles -> {
            var expectedRoles = Arrays.stream(RoleEnum.values())
                    .map(RoleEnum::getName)
                    .toList();

            var givenRoles = iterableToList(roles).stream().map(Role::getName).toList();
            return givenRoles.containsAll(expectedRoles) && expectedRoles.size() == givenRoles.size();
        }));
    }

    @Test
    public void Should_NotAddRoles() {
        var existingRoles = Arrays.stream(RoleEnum.values())
                .map(roleEnum -> {
                    Role role = new Role();
                    role.setName(roleEnum.getName());
                    return role;
                })
                .toList();

        Mockito.when(mockRoleRepository.findAll()).thenReturn(existingRoles);
        rolesConfig.run();
        Mockito.verify(mockRoleRepository, Mockito.never()).saveAll(Mockito.anyCollection());
    }
}
