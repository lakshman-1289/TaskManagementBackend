package com.nrn.submission.service;

import java.util.List;

import com.nrn.submission.model.Submission;

public interface SubmissionService {

    Submission submitTask(Long taskId, String githubLink, Long userId) throws Exception;
    Submission getTaskSubmissionById(Long submissionId) throws Exception;
    List<Submission> getAllTaskSubmissions();
    List<Submission> getTaskSubmissionsByTaskId(Long taskId);
    Submission acceptDeclineSubmission(Long id, String status, String userAuthorities) throws Exception;
}
