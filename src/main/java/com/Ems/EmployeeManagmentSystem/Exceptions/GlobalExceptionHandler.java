package com.Ems.EmployeeManagmentSystem.Exceptions;


import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<?>> handleEmployeeAlreadyExistsException(EmployeeAlreadyExistsException ex) {
        return new ResponseEntity<>(CommonResponse.error(ex.getMessage(), ex.getErrorCode()),ex.getStatus());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return new ResponseEntity<>(CommonResponse.error(ex.getMessage(), ex.getErrorCode()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception ex) {
        return new ResponseEntity<>(CommonResponse.error(ex.getMessage() , "Internal Server Error"),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
                CommonResponse.error("Validation failed", "VALIDATION_ERROR", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(CommonResponse.error("Illegal Argument", "INVALID_ARGUMENT_EXCEPTION", ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<CommonResponse<?>> handleAuthorizationFailedException(AuthenticationFailedException ex) {
        log.warn("Authentication Failed Exception : " + ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error(ex.getMessage(), ex.getErrorCode()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<CommonResponse<?>> handleTokenExpiredException(TokenExpiredException ex) {
        return new ResponseEntity<>(CommonResponse.error(ex.getMessage(), ex.getErrorCode()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied Exception:{}", ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error("You do not have permission to access this resource", "ACCESS_DENIED"),HttpStatus.FORBIDDEN);
    }

}
