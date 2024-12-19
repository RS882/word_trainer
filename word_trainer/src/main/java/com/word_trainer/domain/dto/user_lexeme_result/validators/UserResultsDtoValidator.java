package com.word_trainer.domain.dto.user_lexeme_result.validators;

import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserResultsDtoValidator implements ConstraintValidator<ValidUserResults, UserResultsDto> {

    @Override
    public boolean isValid(UserResultsDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        return dto.getSuccessfulAttempts() <= dto.getAttempts();
    }
}

