package com.nrn.submission.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nrn.submission.dto.TaskDto;

@FeignClient(name = "TASK-SERVICE", url = "http://localhost:5002")
public interface TaskService { 
 @GetMapping("/api/tasks/{id}") 
 TaskDto getTaskById(@PathVariable Long id, @RequestHeader("Authorization") String jwt); 
 
 @PutMapping("/api/tasks/{taskId}/complete") 
 TaskDto completeTask(@PathVariable Long taskId, TaskDto taskDto, @RequestHeader("Authorization") String jwt); 
}