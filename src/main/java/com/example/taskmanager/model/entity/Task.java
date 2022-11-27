package com.example.taskmanager.model.entity;


import com.example.taskmanager.model.dto.TaskDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.NOT_DONE;
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdWhen;
    private LocalDateTime dueDate;
    private LocalDateTime completedWhen;

    public Task(TaskDto taskDto) {
        this.description = taskDto.getDescription();
        this.dueDate = taskDto.getDueDate();
    }

    @SuppressWarnings("unused")
    public enum Status {
        NOT_DONE,
        DONE, PAST_DUE
        }
}
