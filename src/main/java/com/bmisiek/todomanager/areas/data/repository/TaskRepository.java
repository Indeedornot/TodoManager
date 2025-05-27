package com.bmisiek.todomanager.areas.data.repository;

import com.bmisiek.todomanager.areas.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByAssignee_Id(long assigneeId);
}
