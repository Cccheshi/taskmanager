package com.example.taskmanager.model.dto;

import com.example.taskmanager.model.entity.Task;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDto {

    @NotNull
    private String description;
    private LocalDateTime dueDate;
    private UUID id;
    private Task.Status status;
    private LocalDateTime createdWhen;
    private LocalDateTime completedWhen;
}
