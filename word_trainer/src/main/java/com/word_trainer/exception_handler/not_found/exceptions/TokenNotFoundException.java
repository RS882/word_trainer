package com.word_trainer.exception_handler.not_found.exceptions;


import com.word_trainer.exception_handler.not_found.NotFoundException;

public class TokenNotFoundException extends NotFoundException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
