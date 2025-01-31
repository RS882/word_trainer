package com.word_trainer.exception_handler.not_found.exceptions;

import com.word_trainer.exception_handler.not_found.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String email) {
        super(String.format("User with email <%s> not found", email));
    }
    public UserNotFoundException(Long id) {
        super(String.format("User with id <%d> not found", id));
    }
}
