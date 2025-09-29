package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationFailedException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public AuthenticationFailedException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
        this.errorCode = "AuthorizationFailedException";
    }

    public AuthenticationFailedException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public AuthenticationFailedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "AuthorizationFailedException";
    }
}
