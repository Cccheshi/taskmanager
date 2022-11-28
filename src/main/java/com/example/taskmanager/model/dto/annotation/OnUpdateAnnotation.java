package com.example.taskmanager.model.dto.annotation;

import com.example.taskmanager.model.dto.validator.OnUpdateValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OnUpdateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnUpdateAnnotation {
    Class<?>[] groups() default { };
}
