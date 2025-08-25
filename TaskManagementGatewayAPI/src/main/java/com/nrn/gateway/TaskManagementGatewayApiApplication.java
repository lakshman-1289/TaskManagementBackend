package com.nrn.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TaskManagementGatewayApiApplication {

	private static final Logger logger = LogManager.getLogger(TaskManagementGatewayApiApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Task Management Gateway API Application");
		
		try {
			SpringApplication.run(TaskManagementGatewayApiApplication.class, args);
			logger.info("Task Management Gateway API started successfully");
		} catch (Exception e) {
			logger.error("Failed to start Gateway API: {}", e.getMessage(), e);
			throw e;
		}
	}
}
