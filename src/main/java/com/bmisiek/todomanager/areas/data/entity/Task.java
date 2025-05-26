package com.bmisiek.todomanager.areas.data.entity;

import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @ManyToOne
    private Project project;

    @OneToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;
}
