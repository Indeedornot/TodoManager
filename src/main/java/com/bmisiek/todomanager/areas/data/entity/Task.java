package com.bmisiek.todomanager.areas.data.entity;

import com.bmisiek.todomanager.areas.security.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Entity(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @NotNull
    @ManyToOne
    private Project project;

    @NotNull
    @OneToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Nullable
    private ZonedDateTime finishedAt;
}
