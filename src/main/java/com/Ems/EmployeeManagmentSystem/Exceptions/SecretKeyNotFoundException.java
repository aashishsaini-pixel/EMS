package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SecretKeyNotFoundException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public SecretKeyNotFoundException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public SecretKeyNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "SecretKeyNotFoundException";
    }
}
