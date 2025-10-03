package com.Ems.EmployeeManagmentSystem.Exceptions;


import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// IMPORTANT

//"If the user is authenticated with the correct token and tries to access resources they are authorized for, no handler is called — request proceeds normally."
//
//        "If the user is authenticated but tries to access a resource they are NOT authorized for, CustomAccessDeniedHandler is called."
//
//        "If the user is not authenticated but tries to access a protected resource, CustomAuthenticationEntryPoint blocks the request and tells the user to authenticate first."




@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<?>> handleEmployeeAlreadyExistsException(EmployeeAlreadyExistsException ex) {
        return new ResponseEntity<>(CommonResponse.error(true , ex.getMessage(), ex.getErrorCode()),ex.getStatus());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return new ResponseEntity<>(CommonResponse.error(true , ex.getMessage(), ex.getErrorCode()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception ex) {
        log.info("Exception caught in GlobalExceptionHandler");
        log.info("Exeption is : {}", ex.getMessage());
        log.info("Exception : {} ", ex.getStackTrace());
        return new ResponseEntity<>(CommonResponse.error(true , ex.getMessage() , "Internal Server Error"),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
                CommonResponse.validationError(errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(CommonResponse.error(true ,ex.getMessage(), "INVALID_ARGUMENT_EXCEPTION"),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<CommonResponse<?>> handleAuthorizationFailedException(AuthenticationFailedException ex) {
        log.warn("Authentication Failed Exception : " + ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error(false , ex.getMessage(), ex.getErrorCode()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User Not Found Exception : " + ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error(false , ex.getMessage(), ex.getErrorCode()),HttpStatus.NOT_FOUND);
    }


    // This is for method level security , if the method level security fails then this exception is throwend.......:)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied Exception : " + ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error(true , ex.getMessage() , "ACCESS_DENIED"),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<CommonResponse<?>> handleIOException(IOException ex) {
        log.warn("IOException : " + ex.getMessage());
        return new ResponseEntity<>(CommonResponse.error(true , "Failed to export the employee list : " + ex.getMessage(), "IO_EXCEPTION"),HttpStatus.BAD_REQUEST);
    }

}
