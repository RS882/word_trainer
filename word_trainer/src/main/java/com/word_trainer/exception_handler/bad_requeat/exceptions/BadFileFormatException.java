package com.word_trainer.exception_handler.bad_requeat.exceptions;

import com.word_trainer.exception_handler.bad_requeat.BadRequestException;

public class BadFileFormatException extends BadRequestException {
    public BadFileFormatException(String originalFileName) {
        super(String.format("Bad file format: %s", originalFileName));
    }
}
