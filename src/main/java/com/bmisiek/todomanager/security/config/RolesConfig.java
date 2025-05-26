package com.bmisiek.todomanager.security.config;

import com.bmisiek.libraries.seeder.RunOnStartInterface;
import com.bmisiek.todomanager.security.entity.Role;
import com.bmisiek.todomanager.security.entity.RoleEnum;
import com.bmisiek.todomanager.security.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RolesConfig implements RunOnStartInterface {
    private final RoleRepository roleRepository;

    public RolesConfig(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run() {
        var existingRoles = roleRepository.findAll();
        var toAdd = Arrays.stream(RoleEnum.values())
                .filter(roleEnum -> existingRoles.stream().noneMatch(role -> role.getName().equals(roleEnum.getName())))
                .map(roleEnum -> {
                    Role role = new Role();
                    role.setName(roleEnum.getName());
                    return role;
                })
                .toList();

        if (!toAdd.isEmpty()) {
            roleRepository.saveAll(toAdd);
            System.out.println("Added roles: " + toAdd.stream().map(Role::getName).toList());
        } else {
            System.out.println("No new roles to add.");
        }
    }
}
