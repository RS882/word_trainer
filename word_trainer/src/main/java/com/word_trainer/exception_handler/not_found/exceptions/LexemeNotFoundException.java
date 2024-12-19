package com.word_trainer.exception_handler.not_found.exceptions;

import com.word_trainer.exception_handler.not_found.NotFoundException;

import java.util.UUID;

public class LexemeNotFoundException extends NotFoundException {
    public LexemeNotFoundException(UUID lexemeId) {
        super(String.format("Lexeme with id <%s> not found", lexemeId.toString()));
    }
}
