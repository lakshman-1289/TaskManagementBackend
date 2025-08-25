package com.nrn.submission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskSubmissionServiceApplication {

	private static final Logger logger = LogManager.getLogger(TaskSubmissionServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task Submission Service Application");
		
		try {
			SpringApplication.run(TaskSubmissionServiceApplication.class, args);
			logger.info("Task Submission Service started successfully on port 5003");
		} catch (Exception e) {
			logger.error("Failed to start Submission Service: {}", e.getMessage(), e);
			throw e;
		}
	}
}
