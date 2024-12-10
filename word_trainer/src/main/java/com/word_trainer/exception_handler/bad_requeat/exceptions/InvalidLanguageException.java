package com.word_trainer.exception_handler.bad_requeat.exceptions;

import com.word_trainer.exception_handler.bad_requeat.BadRequestException;

public class InvalidLanguageException extends BadRequestException {
    public InvalidLanguageException(String languageValue) {
        super(String.format("Invalid value for Language: %s", languageValue));
    }
}
