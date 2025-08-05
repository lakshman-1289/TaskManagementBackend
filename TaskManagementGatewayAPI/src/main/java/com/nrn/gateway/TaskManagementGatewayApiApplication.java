package com.nrn.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TaskManagementGatewayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementGatewayApiApplication.class, args);
	}

}
