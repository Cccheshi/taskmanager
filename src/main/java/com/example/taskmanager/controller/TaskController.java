package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.dto.groups.UpdateGroup;
import com.example.taskmanager.model.entity.Task.Status;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public void addTask(@RequestBody @Valid TaskDto taskDto) {
        taskService.addTask(taskDto);
    }

    @PatchMapping
    public void updateTask(@RequestBody @Validated(UpdateGroup.class) TaskDto taskDto) {
        taskService.partialUpdate(taskDto);
    }

    @PatchMapping(path = "/{id}/status/{status}")
    public void updateTaskStatus(@PathVariable UUID id, @PathVariable Status status) {
        taskService.updateTaskStatus(id, status);
    }
}
