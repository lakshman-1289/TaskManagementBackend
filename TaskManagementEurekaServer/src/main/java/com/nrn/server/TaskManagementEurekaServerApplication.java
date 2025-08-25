package com.nrn.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TaskManagementEurekaServerApplication {

	private static final Logger logger = LogManager.getLogger(TaskManagementEurekaServerApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task Management Eureka Server Application");
		
		try {
			SpringApplication.run(TaskManagementEurekaServerApplication.class, args);
			logger.info("Task Management Eureka Server started successfully");
		} catch (Exception e) {
			logger.error("Failed to start Eureka Server: {}", e.getMessage(), e);
			throw e;
		}
	}
}
