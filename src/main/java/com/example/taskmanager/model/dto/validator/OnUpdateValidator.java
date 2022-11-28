package com.example.taskmanager.model.dto.validator;

import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.model.dto.annotation.OnUpdateAnnotation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class OnUpdateValidator implements ConstraintValidator<OnUpdateAnnotation, TaskDto> {
    @Override
    public void initialize(OnUpdateAnnotation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TaskDto taskDto, ConstraintValidatorContext constraintValidatorContext) {
       // return taskDto.getId()!=null && (StringUtils.isBlank(taskDto.getDescription()))
        return false;
    }
}
