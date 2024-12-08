package com.word_trainer.exception_handler.bad_requeat.exceptions;


import com.word_trainer.exception_handler.bad_requeat.BadRequestException;

public class BadFileBucketName extends BadRequestException {
    public BadFileBucketName(String message) {
        super(message);
    }
}
