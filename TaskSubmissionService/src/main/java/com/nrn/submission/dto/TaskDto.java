package com.nrn.submission.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
	 private Long id;
	    private String title;
	    private String description;
	    private String image;
	    private Long assignedUserId;
	    private List<String> tags = new ArrayList<>();
	    private LocalDateTime deadline;
	    private LocalDateTime createdAt;
	    private TaskStatus status;

}
