package com.word_trainer.exception_handler.unauthorized;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("User email or password is wrong");
    }
}
