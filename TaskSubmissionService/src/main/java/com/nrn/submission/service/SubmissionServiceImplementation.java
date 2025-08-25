package com.nrn.submission.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrn.submission.client.TaskServiceClient;
import com.nrn.submission.dto.TaskDto;
import com.nrn.submission.model.Submission;
import com.nrn.submission.repository.SubmissionRepository;

@Service
public class SubmissionServiceImplementation implements SubmissionService {

    private static final Logger logger = LogManager.getLogger(SubmissionServiceImplementation.class);

    @Autowired 
    private SubmissionRepository submissionRepository;

    @Autowired 
    private TaskServiceClient taskServiceClient; 

    @Override
    public Submission submitTask(Long taskId, String githubLink, Long userId) throws Exception {
        logger.info("Submitting task ID: {} for user ID: {} with GitHub link: {}", taskId, userId, githubLink);
        
        // Validate task exists by calling TaskService via Feign Client
        try {
            logger.info("Validating task ID: {} exists via TaskService", taskId);
            TaskDto task = taskServiceClient.getTaskById(taskId);
            
            if (task != null && task.getId() != null) {
                logger.info("Task validation successful for task ID: {} - Task title: {}", taskId, task.getTitle());
                
                Submission submission = new Submission();
                submission.setTaskId(taskId);
                submission.setUserId(userId);
                submission.setGithubLink(githubLink);
                submission.setSubmissionTime(LocalDateTime.now());
                submission.setStatus("PENDING");
                
                Submission savedSubmission = submissionRepository.save(submission);
                logger.info("Submission created successfully with ID: {} for task ID: {} by user ID: {}", 
                           savedSubmission.getId(), taskId, userId);
                return savedSubmission;
            } else {
                logger.error("Task validation failed: Task ID {} returned null or invalid response", taskId);
                throw new Exception("Task not found with ID " + taskId);
            }
        } catch (Exception e) {
            logger.error("Failed to validate task ID: {} via TaskService - Error: {}", taskId, e.getMessage(), e);
            throw new Exception("Task not found with ID " + taskId + ": " + e.getMessage());
        }
    }

    @Override
    public Submission getTaskSubmissionById(Long submissionId) throws Exception {
        logger.info("Fetching submission by ID: {}", submissionId);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    logger.error("Submission not found with ID: {}", submissionId);
                    return new Exception("Task submission not found with ID " + submissionId);
                });
        
        logger.info("Submission found: ID {} - Task ID: {} - Status: {}", 
                   submission.getId(), submission.getTaskId(), submission.getStatus());
        return submission;
    }

    @Override
    public List<Submission> getAllTaskSubmissions() {
        logger.info("Fetching all task submissions");
        
        List<Submission> submissions = submissionRepository.findAll();
        logger.info("Total submissions found: {}", submissions.size());
        return submissions;
    }

    @Override
    public List<Submission> getTaskSubmissionsByTaskId(Long taskId) {
        logger.info("Fetching submissions for task ID: {}", taskId);
        
        List<Submission> submissions = submissionRepository.findByTaskId(taskId);
        logger.info("Found {} submissions for task ID: {}", submissions.size(), taskId);
        return submissions;
    }

    @Override
    public Submission acceptDeclineSubmission(Long id, String status, String userAuthorities) throws Exception {
        logger.info("Updating submission ID: {} to status: {} by user with authorities: {}", id, status, userAuthorities);
        
        Submission submission = getTaskSubmissionById(id);
        logger.info("Found submission for update: Task ID {} - Current Status: {}", 
                   submission.getTaskId(), submission.getStatus());
        
        submission.setStatus(status);

        if ("ACCEPTED".equals(status)) {
            logger.info("Accepting submission - completing associated task ID: {} via TaskService", submission.getTaskId());
            try {
                // Call TaskService to complete the task with proper authorities
                TaskDto completedTask = taskServiceClient.completeTask(submission.getTaskId(), userAuthorities);
                logger.info("Task ID: {} successfully completed via TaskService. Status: {}", 
                           submission.getTaskId(), completedTask.getStatus());
            } catch (Exception e) {
                logger.error("Failed to complete task ID: {} via TaskService - Error: {}", 
                           submission.getTaskId(), e.getMessage(), e);
                throw new Exception("Failed to complete task: " + e.getMessage());
            }
        }
        
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission ID: {} status updated successfully to: {}", id, status);
        return savedSubmission;
    }
}
