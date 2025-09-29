package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final HttpStatus status;
    private final String errorCode;

    public UserNotFoundException(String message){
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = "UserNotFoundException";
    }

    public UserNotFoundException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public UserNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "UserNotFoundException";
    }
}
