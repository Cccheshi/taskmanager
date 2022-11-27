package com.example.taskmanager.controller;

import com.example.taskmanager.TaskManagerApplication;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    private static final String PATH = "/api/v1/task";
    private static final String PATH_STATUS_DONE = PATH + "/%s/status/%s";
    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.of(2022, 4, 22, 2, 0);
    private static final String NOT_ALLOWED_ACTION_STATUS_PAS_DUE = "It is not allowed to change status to Past Due.";
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @SuppressWarnings("unused")
    private MockMvc mvc;

    @Test
    public void addTaskTestWhenAllFieldsValidThenAddTask() throws Exception {
        String request = """
                {
                "description": "description",
                "dueDate": "2025-02-02 22:02:02"
                }
                """;
        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void addTaskWhenRequiredFieldsNotExistsThenThrowException() throws Exception {
        String request = """
                {
                }
                """;
        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                                .startsWith("Validation failed"));
    }

    @Test
    public void updateTaskWhenRequiredFieldsExistsThenUpdateTask() throws Exception {
        Task task = findRandomTaskOrCreateNew();
        TaskDto taskDto = taskMapper.mapTaskToTaskDto(task);
        taskDto.setDescription("changed description");
        mvc.perform(MockMvcRequestBuilders.patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        TaskDto updatedTask = taskMapper.mapTaskToTaskDto(taskRepository.findById(task.getId()).orElseThrow());
        Assertions.assertThat(taskDto).isEqualTo(updatedTask);
    }


    @Test
    public void updateTaskWhenTaskNotExistsThenThrowNotFoundException() throws Exception {
        TaskDto taskDto = new TaskDto(UUID.randomUUID(), "description");
        mvc.perform(MockMvcRequestBuilders.patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTaskStatusToDoneWhenTaskNotDoneThenChangeStatus() throws Exception {
        Task task = findRandomTaskOrCreateNew();
        mvc.perform(MockMvcRequestBuilders.patch(String.format(PATH_STATUS_DONE, task.getId().toString(), Task.Status.DONE)))
                .andExpect(status().isOk());
        Assertions.assertThat(taskRepository.findById(task.getId()).orElseThrow().getStatus()).isEqualTo(Task.Status.DONE);
    }

    @Test
    public void updateTaskStatusToPastDueWhenTaskNotDoneThenThrowNotAllowedException() throws Exception {
        Task task = findRandomTaskOrCreateNew();
        mvc.perform(MockMvcRequestBuilders.patch(String.format(PATH_STATUS_DONE, task.getId().toString(), Task.Status.PAST_DUE)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result ->
                        Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                                .contains(NOT_ALLOWED_ACTION_STATUS_PAS_DUE));
    }

    private Task findRandomTaskOrCreateNew() {
        if (taskRepository.findAll().size() == 0) {
            taskRepository.save(new Task("description", DUE_DATE_TIME));
        }
        return taskRepository.findAll().get(0);
    }

}
