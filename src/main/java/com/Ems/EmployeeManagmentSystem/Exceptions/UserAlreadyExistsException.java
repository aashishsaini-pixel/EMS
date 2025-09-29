package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public UserAlreadyExistsException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT; // 409 - Conflict
        this.errorCode = "UserAlreadyExistsException";
    }

    public UserAlreadyExistsException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public UserAlreadyExistsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "UserAlreadyExistsException";
    }
}
