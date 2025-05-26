package com.bmisiek.todomanager.areas.data.repository;

import com.bmisiek.todomanager.areas.data.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Arrays findByOwnerId(long ownerId);

    List<Project> findAllByOwnerId(long ownerId);
}
