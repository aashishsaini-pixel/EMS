package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmployeeAlreadyExistsException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public EmployeeAlreadyExistsException(String message , HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public EmployeeAlreadyExistsException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "EmployeeAlreadyExistsException";
    }
}
