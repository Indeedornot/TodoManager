package com.bmisiek.todomanager.security.entity;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

}
