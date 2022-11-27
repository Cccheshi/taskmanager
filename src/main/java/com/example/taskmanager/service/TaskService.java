package com.example.taskmanager.service;

import com.example.taskmanager.exception.ElementNotFoundException;
import com.example.taskmanager.exception.NotAllowedActionException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private static final String NOT_FOUND_ERROR = "Element with id: %s was not found. %s";
    private static final String NOT_ALLOWED_ACTION_STATUS_PAS_DUE = "It is not allowed to change status to Past Due.";

    public void addTask(TaskDto taskDto) {
        log.info("Add new task {}", taskDto);
        taskRepository.save(taskMapper.mapTaskDtoToTask(taskDto));
    }

    public void partialUpdate(TaskDto taskDto) {
        log.info("Update task {}", taskDto);
        Task task = getTask(taskDto.getId());
        task.setDescription(taskDto.getDescription());
        taskRepository.save(task);

    }

    public void updateTaskStatus(UUID id, Task.Status status) {
        log.info("Update task status. Task ID: {}, status: {}", id, status);
        if (Task.Status.PAST_DUE.equals(status)) {
            throw new NotAllowedActionException(NOT_ALLOWED_ACTION_STATUS_PAS_DUE);
        }
        Task task = getTask(id);
        if (!task.getStatus().equals(status)) {
            task.setStatus(status);
            task.setCompletedWhen(Task.Status.DONE.equals(status) ? LocalDateTime.now() : null);
            taskRepository.save(task);
        }
    }

    public List<TaskDto> getNotCompletedTasks() {
        log.info("Get not completed tasks.");
        List<Task> tasks = taskRepository.findByStatus(Task.Status.NOT_DONE);
        return taskMapper.mapTaskListToTaskDto(tasks);
    }

    public TaskDto getTaskDto(UUID id) {
        log.info("Get task by id {}.", id);
        return taskMapper.mapTaskToTaskDto(getTask(id));
    }

    private Task getTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(String.format(NOT_FOUND_ERROR, id, Task.class)));
    }

    @Scheduled(cron = "${application.scheduler.audit-tasks-status-cron}")
    @SuppressWarnings("unused")
    private void auditTasksStatus() {
        log.info("Task status audit started");
        taskRepository.findAll().forEach(this::statusAudit);
        log.info("Task status audit finished");
    }

    private void statusAudit(Task task) {
        if (LocalDateTime.now().isAfter(task.getDueDate())) {
            task.setStatus(Task.Status.PAST_DUE);
            taskRepository.save(task);
        }
    }

}
