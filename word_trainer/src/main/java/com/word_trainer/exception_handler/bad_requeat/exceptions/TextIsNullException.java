package com.word_trainer.exception_handler.bad_requeat.exceptions;

import com.word_trainer.exception_handler.bad_requeat.BadRequestException;;

public class TextIsNullException extends BadRequestException {
    public TextIsNullException() {
        super("Text is null");
    }
}
