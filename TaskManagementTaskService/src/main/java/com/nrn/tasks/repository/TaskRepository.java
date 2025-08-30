package com.nrn.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nrn.tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Custom query to find tasks by assigned user ID in the list
    @Query("SELECT t FROM Task t WHERE :userId MEMBER OF t.assignedUserIds")
    List<Task> findByAssignedUserIdsContaining(@Param("userId") Long userId);
}