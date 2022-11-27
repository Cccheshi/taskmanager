package com.example.taskmanager.service;

import com.example.taskmanager.exception.ElementNotFoundException;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private static final String NOT_FOUND_ERROR = "Element with id: %s was not found. %s";

    public void addTask(TaskDto taskDto){
        taskRepository.save(new Task(taskDto));
    }

    public void partialUpdate(TaskDto taskDto) {
        Task task = getTask(taskDto.getId());
        task.setDescription(taskDto.getDescription());
        taskRepository.save(task);

    }

    private Task getTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(String.format(NOT_FOUND_ERROR, id, Task.class)));
    }


}
