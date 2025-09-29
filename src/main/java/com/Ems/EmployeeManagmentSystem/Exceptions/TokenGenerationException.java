package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenGenerationException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public TokenGenerationException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
        this.errorCode = "UNAUTHORIZED";
    }

    public TokenGenerationException(String message , HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
