package com.nrn.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskManagementTaskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementTaskServiceApplication.class, args);
	}

}
