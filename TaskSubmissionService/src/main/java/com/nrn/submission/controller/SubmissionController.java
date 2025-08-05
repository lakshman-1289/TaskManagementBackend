package com.nrn.submission.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nrn.submission.dto.UserDto;
import com.nrn.submission.model.Submission;
import com.nrn.submission.service.SubmissionService;
import com.nrn.submission.service.TaskService;
import com.nrn.submission.service.UserService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired 
    private SubmissionService submissionService;

    @Autowired 
    private UserService userService;

    @Autowired 
    private TaskService taskService;

    @PostMapping 
    public ResponseEntity<Submission> submitTask( 
            @RequestParam Long taskId, 
            @RequestParam String githubLink, 
            @RequestHeader("Authorization") String JWT 
    ) throws Exception { 
        UserDto user = userService.getUserProfile(JWT); 
        Submission submission = submissionService.submitTask(taskId, githubLink, user.getId(), JWT);
        return new ResponseEntity<>(submission, HttpStatus.CREATED); 
    }

    @GetMapping("/{id}") 
    public ResponseEntity<Submission> getSubmissionById( 
            @PathVariable Long id 
    ) throws Exception { 
        Submission submission = submissionService.getTaskSubmissionById(id); 
        return new ResponseEntity<>(submission, HttpStatus.OK); 
    }

    @GetMapping 
    public ResponseEntity<List<Submission>> getAllSubmissions( 
            @RequestHeader("Authorization") String jwt 
    ) { 
        List<Submission> submissions = submissionService.getAllTaskSubmissions(); 
        return new ResponseEntity<>(submissions, HttpStatus.OK); 
    }

    @GetMapping("/task/{taskId}") 
    public ResponseEntity<List<Submission>> getTaskSubmissionsByTaskId( 
            @PathVariable Long taskId, 
            @RequestHeader("Authorization") String jwt 
    ) { 
        List<Submission> submissions = submissionService.getTaskSubmissionsByTaskId(taskId); 
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @PutMapping("/{id}") 
    public ResponseEntity<Submission> acceptOrDeclineSubmission( 
            @PathVariable Long id, 
            @RequestParam String status,
            @RequestHeader("Authorization") String jwt 
    ) throws Exception { 
        Submission submission = submissionService.acceptDeclineSubmission(id, status); 
        return new ResponseEntity<>(submission, HttpStatus.OK); 
    }
    
    @GetMapping("/")
	public ResponseEntity<String> welcome(){
		return new ResponseEntity<>("Welcome to submission servie", HttpStatus.OK); 
	}
}
