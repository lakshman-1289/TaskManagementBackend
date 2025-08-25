package com.nrn.submission.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nrn.submission.dto.TaskDto;

@FeignClient(name = "TASK-SERVICE", url = "http://localhost:5002")
public interface TaskServiceClient {

    @GetMapping("/api/tasks/{id}")
    TaskDto getTaskById(@PathVariable("id") Long id);

    @PutMapping("/api/tasks/{id}/complete")
    TaskDto completeTask(
        @PathVariable("id") Long id,
        @RequestHeader("X-User-Authorities") String userAuthorities
    );
}