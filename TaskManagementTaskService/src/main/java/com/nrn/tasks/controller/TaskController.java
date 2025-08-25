package com.nrn.tasks.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nrn.tasks.model.Task;
import com.nrn.tasks.model.TaskStatus;
import com.nrn.tasks.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody Task task,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Creating new task: {}", task.getTitle());
        
        try {
            if (task == null || task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                logger.error("Invalid task data provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Task createdTask = taskService.createTask(task, userAuthorities);
            logger.info("Task created successfully with ID: {}", createdTask.getId());
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating task: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        logger.info("Fetching task with ID: {}", id);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid task ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Task task = taskService.getTaskById(id);
            logger.info("Task fetched successfully: {}", task.getTitle());
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching task with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Task>> getAssignedUsersTask(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) TaskStatus status) {
        logger.info("Fetching tasks for user ID: {} with status: {}", userId, status);
        
        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("No user ID provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<Task> tasks = taskService.getAssignedUsersTask(Long.valueOf(userId), status);
            logger.info("Found {} tasks for user ID: {}", tasks.size(), userId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID format: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error fetching tasks for user ID {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        logger.info("Fetching all tasks with status: {}", status);
        
        try {
            List<Task> tasks = taskService.getAllTask(status);
            logger.info("Found {} tasks", tasks.size());
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all tasks: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Task> assignTaskToUser(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Assigning task ID: {} to user ID: {}", id, userId);
        
        try {
            // Validate input parameters
            if (id == null || id <= 0 || userId == null || userId <= 0) {
                logger.error("Invalid task ID: {} or user ID: {}", id, userId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Validate that only admins can assign tasks
            if (userAuthorities == null || !userAuthorities.contains("ROLE_ADMIN")) {
                logger.warn("Unauthorized task assignment attempt by user with authorities: {}", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Task task = taskService.assignToUser(userId, id);
            logger.info("Task ID: {} successfully assigned to user ID: {}", id, userId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error assigning task ID {} to user ID {}: {}", id, userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask,
            @RequestHeader("X-User-Id") String userId) {
        logger.info("Updating task ID: {} by user ID: {}", id, userId);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid task ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("No user ID provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            if (updatedTask == null) {
                logger.error("No task data provided for update");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Task task = taskService.updateTask(id, updatedTask, Long.valueOf(userId));
            logger.info("Task ID: {} updated successfully", id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID format: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error updating task ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Completing task ID: {}", id);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid task ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Allow admins or task owners to complete tasks
            // Admin check is basic - in real scenario, you'd also check task ownership
            if (userAuthorities == null || (!userAuthorities.contains("ROLE_ADMIN") && !userAuthorities.contains("ROLE_USER"))) {
                logger.warn("Unauthorized task completion attempt by user with authorities: {}", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Task task = taskService.completeTask(id);
            logger.info("Task ID: {} completed successfully", id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error completing task ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Deleting task ID: {}", id);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid task ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Only admins can delete tasks
            if (userAuthorities == null || !userAuthorities.contains("ROLE_ADMIN")) {
                logger.warn("Unauthorized task deletion attempt by user with authorities: {}", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            taskService.deleteTask(id);
            logger.info("Task ID: {} deleted successfully", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting task ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}