package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(TaskDto taskDto){
        taskRepository.save(new Task(taskDto));
    }
}
