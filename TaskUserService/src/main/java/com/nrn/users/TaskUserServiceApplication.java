package com.nrn.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskUserServiceApplication {

	private static final Logger logger = LogManager.getLogger(TaskUserServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task User Service Application");
		
		try {
			SpringApplication.run(TaskUserServiceApplication.class, args);
			logger.info("Task User Service started successfully on port 5001");
		} catch (Exception e) {
			logger.error("Failed to start User Service: {}", e.getMessage(), e);
			throw e;
		}
	}
}
