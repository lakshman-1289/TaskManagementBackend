package com.nrn.submission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nrn.submission.model.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> { 

	 List<Submission> findByTaskIdAndStatus(Long taskId, String status);
}