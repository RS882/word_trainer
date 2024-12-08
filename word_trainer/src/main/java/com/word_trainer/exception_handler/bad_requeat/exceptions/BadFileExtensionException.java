package com.word_trainer.exception_handler.bad_requeat.exceptions;

import com.word_trainer.exception_handler.bad_requeat.BadRequestException;

public class BadFileExtensionException extends BadRequestException {
    public BadFileExtensionException(String fileExtension) {
        super(String.format("File extension <%s> must equals current file extension", fileExtension));

    }
}
