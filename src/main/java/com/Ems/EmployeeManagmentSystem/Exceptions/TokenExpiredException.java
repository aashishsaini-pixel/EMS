package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenExpiredException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public TokenExpiredException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED; // 401 - Unauthorized
        this.errorCode = "TokenExpiredException";
    }

    public TokenExpiredException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public TokenExpiredException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "TokenExpiredException";
    }
}
