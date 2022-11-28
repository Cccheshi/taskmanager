package com.example.taskmanager.model.dto;

import com.example.taskmanager.model.dto.groups.UpdatingGroup;
import com.example.taskmanager.model.entity.Task;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDto {

    @NotNull(groups = UpdatingGroup.class, message = "Field id can not be null")
    private UUID id;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    private Task.Status status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdWhen;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedWhen;

    public TaskDto(UUID id, String description) {
        this.id = id;
        this.description = description;
    }
}
