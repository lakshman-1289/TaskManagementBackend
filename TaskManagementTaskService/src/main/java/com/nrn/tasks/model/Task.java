package com.nrn.tasks.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String image;
    
    // Changed from single assigned user to list of assigned users
    @ElementCollection
    private List<Long> assignedUserIds = new ArrayList<>();
    
    private List<String> tags = new ArrayList<>();
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private TaskStatus status;
}