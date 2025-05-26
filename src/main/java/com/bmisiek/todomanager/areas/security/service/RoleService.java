package com.bmisiek.todomanager.areas.security.service;

import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByEnum(RoleEnum roleEnum) {
        return roleRepository.findByName(roleEnum.getName());
    }
}
