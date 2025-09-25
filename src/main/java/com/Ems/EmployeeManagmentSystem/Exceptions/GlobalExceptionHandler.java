package com.Ems.EmployeeManagmentSystem.Exceptions;


import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
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

}
