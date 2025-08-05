package com.nrn.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrn.tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
	    List<Task> findByAssignedUserId(Long userId);
}


