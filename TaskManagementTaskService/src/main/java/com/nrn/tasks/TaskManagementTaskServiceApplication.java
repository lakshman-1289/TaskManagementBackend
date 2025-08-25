package com.nrn.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementTaskServiceApplication {

	private static final Logger logger = LogManager.getLogger(TaskManagementTaskServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task Management Task Service Application");
		
		try {
			SpringApplication.run(TaskManagementTaskServiceApplication.class, args);
			logger.info("Task Management Task Service started successfully on port 5002");
		} catch (Exception e) {
			logger.error("Failed to start Task Service: {}", e.getMessage(), e);
			throw e;
		}
	}
}
