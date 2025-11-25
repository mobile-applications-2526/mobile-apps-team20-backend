package com.mbproyect.campusconnect.config.exceptions.handler;



import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mbproyect.campusconnect.config.exceptions.auth.InvalidGoogleTokenException;
import com.mbproyect.campusconnect.config.exceptions.chat.ChatNotFoundException;
import com.mbproyect.campusconnect.config.exceptions.event.*;
import com.mbproyect.campusconnect.config.exceptions.auth.InvalidTokenException;
import com.mbproyect.campusconnect.config.exceptions.user.UserAlreadyExistsException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to create an ErrorResponse
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(
            RuntimeException exception,
            WebRequest request,
            HttpStatus status
    ) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler({
            InvalidFormatException.class,
            ParticipantAlreadyExistsException.class,
            MethodArgumentNotValidException.class,      // Json body validation
            ConstraintViolationException.class,         // Request parameters validation
            MethodArgumentTypeMismatchException.class,   // When cannot convert a request parameter to object
            MissingServletRequestParameterException.class,
            InvalidDateException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            InvalidGoogleTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            IllegalStateException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handlerAuthorisationException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            EventNotFoundException.class,
            EventCancelledException.class,
            UserNotFoundException.class,
            ChatNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.NOT_FOUND);
    }

    // Other exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(RuntimeException ex, WebRequest request) {
        return createErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
