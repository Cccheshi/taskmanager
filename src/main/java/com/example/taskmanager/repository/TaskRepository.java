package com.example.taskmanager.repository;

import com.example.taskmanager.model.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

@Repository
public interface TaskRepository extends CrudRepository<Task, UUID> {
    @NonNull
    List<Task> findAll();

    List<Task> findByStatus(Task.Status status);

    List<Task> findByStatusAndDueDateNotNull(Task.Status status);

}
