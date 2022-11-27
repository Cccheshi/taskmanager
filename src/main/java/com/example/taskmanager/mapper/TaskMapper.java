package com.example.taskmanager.mapper;

import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completedWhen", ignore = true)
    @Mapping(target = "createdWhen", ignore = true)
    @Mapping(target = "status", ignore = true)
    Task mapTaskDtoToTask(TaskDto taskDto);
    TaskDto mapTaskToTaskDto(Task task);
    List<TaskDto> mapTaskListToTaskDto(List<Task> tasks);
}
