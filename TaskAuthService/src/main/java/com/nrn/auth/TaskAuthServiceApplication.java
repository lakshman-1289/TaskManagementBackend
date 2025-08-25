package com.nrn.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TaskAuthServiceApplication {

	private static final Logger logger = LogManager.getLogger(TaskAuthServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task Authentication Service Application");
		
		try {
			SpringApplication.run(TaskAuthServiceApplication.class, args);
			logger.info("Task Authentication Service started successfully on port 5004");
		} catch (Exception e) {
			logger.error("Failed to start Authentication Service: {}", e.getMessage(), e);
			throw e;
		}
	}
}