package com.word_trainer.controllers.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Enum<?>[] enumValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        this.enumValues = enumClass.getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        boolean isValid = Arrays.stream(enumValues)
                .map(Enum::name)
                .anyMatch(enumValue -> enumValue.equalsIgnoreCase(value));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid language code. Allowed values: " + Arrays.toString(enumValues))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
