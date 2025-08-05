package com.nrn.tasks.service;

import java.util.List;

import com.nrn.tasks.model.Task;
import com.nrn.tasks.model.TaskStatus;

public interface TaskService {

	    Task createTask(Task task, String requestRole) throws Exception;
	    Task getTaskById(Long id) throws Exception;
	    List<Task> getAllTask(TaskStatus status);
	    Task updateTask(Long id, Task updatedTask, Long userId) throws Exception;
	    void deleteTask(Long id) throws Exception;
	    Task assignToUser(Long userId, Long taskId) throws Exception;
	    List<Task> getAssignedUsersTask(Long userId, TaskStatus status);
	    Task completeTask(Long taskId) throws Exception;
	}
