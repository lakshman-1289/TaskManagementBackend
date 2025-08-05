package com.nrn.submission.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrn.submission.dto.TaskDto;
import com.nrn.submission.model.Submission;
import com.nrn.submission.repository.SubmissionRepository;

@Service
public class SubmissionServiceImplementation implements SubmissionService { 

    @Autowired 
    private SubmissionRepository submissionRepository; 

    @Autowired 
    private TaskService taskService; 

    @Autowired
    private UserService userService; 

    @Override
    public Submission submitTask(Long taskId, String githubLink, Long userId, String jwt) throws Exception { 
        TaskDto task = taskService.getTaskById(taskId, jwt); 

        if (task != null) { 
            Submission submission = new Submission(); 
            submission.setTaskId(taskId); 
            submission.setUserId(userId);
            submission.setGithubLink(githubLink); 
            submission.setSubmissionTime(LocalDateTime.now()); 
            submission.setStatus("PENDING");
            return submissionRepository.save(submission); 
        }
        throw new Exception("Task not found with ID " + taskId); 
    }

    @Override
    public Submission getTaskSubmissionById(Long submissionId) throws Exception { 
        return submissionRepository.findById(submissionId) 
                .orElseThrow(() -> new Exception("Task submission not found with ID " + submissionId));
    }

    @Override
    public List<Submission> getAllTaskSubmissions() { 
        return submissionRepository.findAll(); 
    }

    @Override
    public List<Submission> getTaskSubmissionsByTaskId(Long taskId) { 
        return submissionRepository.findByTaskId(taskId); 
    }

    @Override
    public Submission acceptDeclineSubmission(Long id, String status) throws Exception { 
        Submission submission = getTaskSubmissionById(id); 
        submission.setStatus(status); 

        if (status.equals("accept")) { 
            taskService.completeTask(submission.getTaskId(), new TaskDto(), "JWT_TOKEN"); 
        }
        return submissionRepository.save(submission);
    }
}
