package com.bmisiek.todomanager.areas.data.entity;

import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
