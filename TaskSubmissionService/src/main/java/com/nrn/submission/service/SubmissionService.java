package com.nrn.submission.service;

import java.util.List;

import com.nrn.submission.model.Submission;

public interface SubmissionService { // Interface definition [13]

    Submission submitTask(Long taskId, String githubLink, Long userId, String jwt) throws Exception; // Method signature [13, 14]
    Submission getTaskSubmissionById(Long submissionId) throws Exception; // Method signature [13, 15]
    List<Submission> getAllTaskSubmissions(); // Method signature [15, 16]
    List<Submission> getTaskSubmissionsByTaskId(Long taskId); // Method signature [15, 16]
    Submission acceptDeclineSubmission(Long id, String status) throws Exception; // Method signature [16, 17]
}
