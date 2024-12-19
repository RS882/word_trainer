package com.word_trainer.domain.dto.user_lexeme_result.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserResultsDtoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserResults {
    String message() default "Successful attempts cannot be greater than attempts";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

