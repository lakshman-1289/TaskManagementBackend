package com.nrn.submission.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nrn.submission.model.Submission;
import com.nrn.submission.service.SubmissionService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private static final Logger logger = LogManager.getLogger(SubmissionController.class);

    @Autowired 
    private SubmissionService submissionService;


    @PostMapping
    public ResponseEntity<Submission> submitTask(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId
    ) {
        logger.info("Submitting task (JSON format) by user ID: {} with request: {}", userId, request);
        
        try {
            // Extract values from JSON
            Long taskId = null;
            String githubLink = null;
            
            if (request.containsKey("taskId")) {
                Object taskIdObj = request.get("taskId");
                if (taskIdObj instanceof Number) {
                    taskId = ((Number) taskIdObj).longValue();
                } else if (taskIdObj instanceof String) {
                    taskId = Long.valueOf((String) taskIdObj);
                }
            }
            
            if (request.containsKey("githubLink")) {
                githubLink = (String) request.get("githubLink");
            }
            
            // Input validation
            if (taskId == null || taskId <= 0) {
                logger.error("Invalid task ID: {}", taskId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("No user ID provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            if (githubLink == null || githubLink.trim().isEmpty()) {
                logger.error("No GitHub link provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Basic GitHub URL validation
            if (!githubLink.contains("github.com")) {
                logger.error("Invalid GitHub link format: {}", githubLink);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Long userIdLong = Long.valueOf(userId);
            Submission submission = submissionService.submitTask(taskId, githubLink, userIdLong);
            logger.info("Task submission created successfully with ID: {} (JSON format)", submission.getId());
            return new ResponseEntity<>(submission, HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID format: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error submitting task for user ID {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}") 
    public ResponseEntity<Submission> getSubmissionById( 
            @PathVariable Long id 
    ) {
        logger.info("Fetching submission with ID: {}", id);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid submission ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Submission submission = submissionService.getTaskSubmissionById(id);
            logger.info("Submission fetched successfully: ID {}", submission.getId());
            return new ResponseEntity<>(submission, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching submission with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping 
    public ResponseEntity<List<Submission>> getAllSubmissions(
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Fetching all submissions by user with authorities: {}", userAuthorities);
        
        try {
            // Check for admin role - only admins can view all submissions
            if (userAuthorities == null || !userAuthorities.contains("ROLE_ADMIN")) {
                logger.warn("Unauthorized access attempt to getAllSubmissions by user with authorities: {}", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            List<Submission> submissions = submissionService.getAllTaskSubmissions();
            logger.info("Found {} submissions for admin user", submissions.size());
            return new ResponseEntity<>(submissions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all submissions: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/task/{taskId}") 
    public ResponseEntity<List<Submission>> getTaskSubmissionsByTaskId(
            @PathVariable Long taskId) {
        logger.info("Fetching submissions for task ID: {}", taskId);
        
        try {
            if (taskId == null || taskId <= 0) {
                logger.error("Invalid task ID: {}", taskId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<Submission> submissions = submissionService.getTaskSubmissionsByTaskId(taskId);
            logger.info("Found {} submissions for task ID: {}", submissions.size(), taskId);
            return new ResponseEntity<>(submissions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching submissions for task ID {}: {}", taskId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}") 
    public ResponseEntity<Submission> acceptOrDeclineSubmission( 
            @PathVariable Long id, 
            @RequestParam String status,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Updating submission ID: {} to status: {}", id, status);
        
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid submission ID: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            if (status == null || status.trim().isEmpty()) {
                logger.error("No status provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Validate status values
            if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("DECLINED")) {
                logger.error("Invalid status value: {}. Must be ACCEPTED or DECLINED", status);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Only admins can accept/decline submissions
            if (userAuthorities == null || !userAuthorities.contains("ROLE_ADMIN")) {
                logger.warn("Unauthorized submission status update attempt by user with authorities: {}", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Submission submission = submissionService.acceptDeclineSubmission(id, status, userAuthorities);
            logger.info("Submission ID: {} status updated to: {}", id, status);
            return new ResponseEntity<>(submission, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating submission ID {} to status {}: {}", id, status, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/")
	public ResponseEntity<String> welcome(){
		logger.info("Welcome endpoint accessed");
		return new ResponseEntity<>("Welcome to submission service", HttpStatus.OK); 
	}
}
