package com.nrn.tasks.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrn.tasks.model.Task;
import com.nrn.tasks.model.TaskStatus;
import com.nrn.tasks.repository.TaskRepository;

@Service
public class TaskServiceImplementation implements TaskService {

    private static final Logger logger = LogManager.getLogger(TaskServiceImplementation.class);

    @Autowired 	
    private TaskRepository taskRepository; 	

    @Override
    public Task createTask(Task task, String requestRole) throws Exception {
        logger.info("Creating new task with title: {} by role: {}", task != null ? task.getTitle() : "null", requestRole);
        
        if (!requestRole.equals("ROLE_ADMIN")) {
            logger.warn("Task creation denied - user role: {} is not admin", requestRole);
            throw new Exception("Only admin can create tasks");
        }

        logger.info("Setting task status to PENDING and creation time");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {} and title: {}", savedTask.getId(), savedTask.getTitle());
        return savedTask;
    }

    @Override 
    public Task getTaskById(Long id) throws Exception {
        logger.info("Fetching task by ID: {}", id);
        
        Task task = taskRepository.findById(id).orElseThrow(() -> {
            logger.error("Task not found with ID: {}", id);
            return new Exception("Task not found with ID " + id);
        });
        
        logger.info("Task found: ID {} - Title: {}", task.getId(), task.getTitle());
        return task;
    }

    @Override
    public List<Task> getAllTask(TaskStatus status) {
        logger.info("Fetching all tasks with status filter: {}", status);
        
        List<Task> allTask = taskRepository.findAll();
        logger.info("Total tasks in database: {}", allTask.size());

        List<Task> filteredTasks = allTask.stream().filter(
            task -> status == null || task.getStatus().name().equalsIgnoreCase(status.toString())
        ).collect(Collectors.toList());
        
        logger.info("Filtered tasks count: {} (filter: {})", filteredTasks.size(), status);
        return filteredTasks;
    }

    @Override
    public Task updateTask(Long id, Task updatedTask, Long userId) throws Exception {
        logger.info("Updating task ID: {} by user ID: {}", id, userId);
        
        Task existingTask = getTaskById(id);
        logger.info("Found existing task: {}", existingTask.getTitle());

        if (updatedTask.getTitle() != null) {
            logger.info("Updating task title from '{}' to '{}'", existingTask.getTitle(), updatedTask.getTitle());
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getImage() != null) {
            logger.info("Updating task image");
            existingTask.setImage(updatedTask.getImage());
        }
        if (updatedTask.getDescription() != null) {
            logger.info("Updating task description");
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getStatus() != null) {
            logger.info("Updating task status from '{}' to '{}'", existingTask.getStatus(), updatedTask.getStatus());
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getDeadline() != null) {
            logger.info("Updating task deadline to: {}", updatedTask.getDeadline());
            existingTask.setDeadline(updatedTask.getDeadline());
        }

        Task savedTask = taskRepository.save(existingTask);
        logger.info("Task ID: {} updated successfully", savedTask.getId());
        return savedTask;
    }

    @Override
    public void deleteTask(Long id) throws Exception {
        logger.info("Deleting task with ID: {}", id);
        
        Task task = getTaskById(id);
        logger.info("Task found for deletion: {}", task.getTitle());
        
        taskRepository.deleteById(id);
        logger.info("Task ID: {} deleted successfully", id);
    }

    @Override
    public Task assignToUser(Long userId, Long taskId) throws Exception {
        logger.info("Assigning task ID: {} to user ID: {}", taskId, userId);
        
        Task existingTask = getTaskById(taskId);
        logger.info("Found task for assignment: {}", existingTask.getTitle());
        
        // Add user to the list of assigned users instead of replacing
        if (!existingTask.getAssignedUserIds().contains(userId)) {
            existingTask.getAssignedUserIds().add(userId);
            // Only change status to ASSIGNED if it was PENDING
            if (existingTask.getStatus() == TaskStatus.PENDING) {
                existingTask.setStatus(TaskStatus.ASSIGNED);
            }
        }
        
        Task savedTask = taskRepository.save(existingTask);
        logger.info("Task ID: {} successfully assigned to user ID: {}", taskId, userId);
        return savedTask;
    }

    @Override
    public List<Task> getAssignedUsersTask(Long userId, TaskStatus status) {
        logger.info("Fetching assigned tasks for user ID: {} with status: {}", userId, status);
        
        // Use the new repository method for better performance
        List<Task> allTask = taskRepository.findByAssignedUserIdsContaining(userId);
            
        logger.info("Total assigned tasks for user ID {}: {}", userId, allTask.size());

        List<Task> filteredTasks = allTask.stream().filter(
                task -> status == null || task.getStatus().name().equalsIgnoreCase(status.toString())
        ).collect(Collectors.toList());
        
        logger.info("Filtered assigned tasks for user ID {}: {} (filter: {})", userId, filteredTasks.size(), status);
        return filteredTasks;
    }

    @Override
    public Task completeTask(Long taskId) throws Exception {
        logger.info("Completing task with ID: {}", taskId);
        
        Task task = getTaskById(taskId);
        logger.info("Found task for completion: {}", task.getTitle());
        
        task.setStatus(TaskStatus.DONE);
        Task savedTask = taskRepository.save(task);
        
        logger.info("Task ID: {} completed successfully with status: {}", taskId, savedTask.getStatus());
        return savedTask;
    }
}