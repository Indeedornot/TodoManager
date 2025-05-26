package com.bmisiek.todomanager.areas.data.repository;

import com.bmisiek.todomanager.areas.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
