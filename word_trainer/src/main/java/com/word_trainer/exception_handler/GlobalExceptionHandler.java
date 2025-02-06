package com.word_trainer.exception_handler;


import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.exception_handler.bad_requeat.BadRequestException;
import com.word_trainer.exception_handler.dto.ValidationErrorDto;
import com.word_trainer.exception_handler.dto.ValidationErrorsDto;
import com.word_trainer.exception_handler.forbidden.ForbiddenException;
import com.word_trainer.exception_handler.not_found.NotFoundException;
import com.word_trainer.exception_handler.server_exception.ServerIOException;
import com.word_trainer.exception_handler.unauthorized.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(@Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseMessageDto> handleNotFoundException(AuthenticationException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseMessageDto> handleNotFoundException(ForbiddenException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessageDto> handleException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ResponseMessageDto> handleException(MissingRequestCookieException e) {
        return new ResponseEntity<>(new ResponseMessageDto("Cookie is incorrect"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessageDto> handleException(BadRequestException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseMessageDto> handleException(NotFoundException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseMessageDto> handleException(UnauthorizedException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ServerIOException.class)
    public ResponseEntity<ResponseMessageDto> handleException(ServerIOException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorsDto> handleValidationException(MethodArgumentNotValidException e) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;

            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .field(fieldError.getField())
                    .message("Field " + fieldError.getDefaultMessage())
                    .build();
            if (fieldError.getRejectedValue() != null)
                errorDto.setRejectedValue(fieldError.getRejectedValue().toString());

            validationErrors.add(errorDto);
        }
        return ResponseEntity.badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseMessageDto> handleException(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(new ResponseMessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorsDto> handleValidationException(HandlerMethodValidationException ex) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();

        ex.getParameterValidationResults().forEach(vr -> {
                    String parameterName = vr.getMethodParameter().getParameterName();
                    String message = vr.getResolvableErrors().stream()
                            .map(MessageSourceResolvable::getDefaultMessage)
                            .findFirst()
                            .orElse(null);

                    ValidationErrorDto errorDto = ValidationErrorDto.builder()
                            .field(parameterName)
                            .message(
                                    parameterName + ": " + (message == null ? "is wrong" : message))
                            .build();
                    validationErrors.add(errorDto);
                }
        );
        return ResponseEntity.badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());

    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ResponseMessageDto> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException e) {
        return new ResponseEntity<>(new ResponseMessageDto("Query error: Please check that all fields in the query exist."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessageDto> handleException(RuntimeException e) {
        log.error("RuntimeException occurred", e);
        return new ResponseEntity<>(new ResponseMessageDto("Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
