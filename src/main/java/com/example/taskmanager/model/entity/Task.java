package com.example.taskmanager.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    @CreationTimestamp
    private LocalDateTime createdWhen;
    private LocalDateTime dueDate;
    private LocalDateTime completedWhen;


    @SuppressWarnings("unused")
    public enum Status {
        NOT_DONE, DONE, PAST_DUE
        }
}
