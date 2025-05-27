package com.bmisiek.todomanager.areas.data.repository;

import com.bmisiek.todomanager.areas.data.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOwnerId(long ownerId);

    List<Project> findAllByTasksAssignee_Id(long tasksId);
}
