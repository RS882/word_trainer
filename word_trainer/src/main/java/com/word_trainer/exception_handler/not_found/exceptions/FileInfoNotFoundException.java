package com.word_trainer.exception_handler.not_found.exceptions;

import com.word_trainer.exception_handler.not_found.NotFoundException;

import java.util.UUID;

public class FileInfoNotFoundException extends NotFoundException {
    public FileInfoNotFoundException(UUID fileId) {
        super(String.format("File with ID <%s> not found", fileId));
    }
}
