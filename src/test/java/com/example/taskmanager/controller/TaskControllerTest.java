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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TaskManagerApplication.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    private static final String PATH = "/api/v1/task";
    private static final String PATH_STATUS = "/api/v1/task/%s/status/%s";
    private static final String PATH_TASK_BY_ID = "/api/v1/task/%s";
    private static final LocalDateTime DUE_DATE_TIME_FUTURE = LocalDateTime.now().plusYears(3);
    private static final LocalDateTime DUE_DATE_TIME_PAST = LocalDateTime.now().minusYears(2);
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
    public void updateTaskWhenDueDateInFutureThenUpdateTask() throws Exception {
        Task task = findRandomTaskOrCreateNew(DUE_DATE_TIME_FUTURE);
        TaskDto taskDto = taskMapper.mapTaskToTaskDto(task);
        taskDto.setDescription("changed description");
        taskDto.setDueDate(DUE_DATE_TIME_FUTURE);
        mvc.perform(MockMvcRequestBuilders.put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        TaskDto updatedTask = taskMapper.mapTaskToTaskDto(taskRepository.findById(task.getId()).orElseThrow());
        Assertions.assertThat(Task.Status.NOT_DONE).isEqualTo(updatedTask.getStatus());
    }

    @Test
    public void updateTaskWhenTaskNotExistsThenThrowNotFoundException() throws Exception {
        TaskDto taskDto = new TaskDto(UUID.randomUUID(), "description");
        mvc.perform(MockMvcRequestBuilders.put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTaskStatusToDoneWhenTaskNotDoneThenChangeStatus() throws Exception {
        Task task = findRandomNotDoneTaskOrCreateNew();
        mvc.perform(MockMvcRequestBuilders
                        .patch(String.format(PATH_STATUS, task.getId(), Task.Status.DONE)))
                .andExpect(status().isOk());
        Task taskAfter = taskRepository.findById(task.getId()).orElseThrow();
        Assertions.assertThat(taskAfter.getStatus())
                .isEqualTo(Task.Status.DONE);
        Assertions.assertThat(taskAfter.getCompletedWhen()).isNotNull();
    }

    @Test
    public void updateTaskStatusToNotDoneWhenTaskCompletedAndDueDateInFutureThenChangeStatus() throws Exception {
        Task task = findRandomTaskOrCreateNew(DUE_DATE_TIME_FUTURE);
        mvc.perform(MockMvcRequestBuilders
                        .patch(String.format(PATH_STATUS, task.getId(), Task.Status.DONE)))
                .andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                        .patch(String.format(PATH_STATUS, task.getId(), Task.Status.NOT_DONE)))
                .andExpect(status().isOk());
        Task taskAfter = taskRepository.findById(task.getId()).orElseThrow();
        Assertions.assertThat(taskAfter.getStatus())
                .isEqualTo(Task.Status.NOT_DONE);
        Assertions.assertThat(taskAfter.getCompletedWhen()).isNull();
    }

    @Test
    public void updateTaskStatusToNotDoneWhenTaskCompletedAndDueDateInPastThenChangeStatusToPastDue() throws Exception {
        Task task = findRandomTaskOrCreateNew(DUE_DATE_TIME_PAST);
        mvc.perform(MockMvcRequestBuilders
                        .patch(String.format(PATH_STATUS, task.getId(), Task.Status.DONE)))
                .andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders
                        .patch(String.format(PATH_STATUS, task.getId(), Task.Status.NOT_DONE)))
                .andExpect(status().isOk());
        Task taskAfter = taskRepository.findById(task.getId()).orElseThrow();
        Assertions.assertThat(taskAfter.getStatus())
                .isEqualTo(Task.Status.PAST_DUE);
        Assertions.assertThat(taskAfter.getCompletedWhen()).isNull();
    }

    @Test
    public void updateTaskStatusToPastDueWhenTaskNotDoneThenThrowNotAllowedException() throws Exception {
        Task task = findRandomTaskOrCreateNew(DUE_DATE_TIME_FUTURE);
        mvc.perform(MockMvcRequestBuilders
                .patch(String.format(PATH_STATUS, task.getId(), Task.Status.PAST_DUE)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result ->
                        Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                                .contains(NOT_ALLOWED_ACTION_STATUS_PAS_DUE));
    }

    @Test
    public void getTaskByIdWhenTaskExistsThenReturnTask() throws Exception {
        Task task = findRandomTaskOrCreateNew(DUE_DATE_TIME_FUTURE);
        mvc.perform(MockMvcRequestBuilders
                        .get(String.format(PATH_TASK_BY_ID, task.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getTaskByIdWhenTaskNotExistsThenThrowNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(String.format(PATH_TASK_BY_ID, UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    private Task findRandomNotDoneTaskOrCreateNew() {
        List<Task> tasks = taskRepository.findByStatus(Task.Status.NOT_DONE);
        if (tasks.size() == 0) {
           return createNewTask(DUE_DATE_TIME_FUTURE);
        }else {
            return tasks.get(0);
        }
    }

    private Task findRandomTaskOrCreateNew(LocalDateTime localDateTime) {
       List<Task> tasks = taskRepository.findAll()
               .stream()
               .filter(task -> task.getDueDate().isEqual(localDateTime)).toList();
        if (tasks.size() == 0) {
            return createNewTask(localDateTime);
        } else {
            return tasks.get(0);
        }
    }

    private Task createNewTask(LocalDateTime localDateTime) {
       Task task = new Task("description", localDateTime);
        taskRepository.save(task);
        return task;
    }

}
