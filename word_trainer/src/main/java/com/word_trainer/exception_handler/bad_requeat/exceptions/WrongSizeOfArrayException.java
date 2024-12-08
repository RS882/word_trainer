package com.word_trainer.exception_handler.bad_requeat.exceptions;

import com.word_trainer.exception_handler.bad_requeat.BadRequestException;

public class WrongSizeOfArrayException extends BadRequestException {
    public WrongSizeOfArrayException(long size) {
        super(String.format("The array must contain exactly %d elements.",size));
    }
}
