package com.example.taskmanager.mapper;

import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto mapTaskToTaskDto(Task task);
}
