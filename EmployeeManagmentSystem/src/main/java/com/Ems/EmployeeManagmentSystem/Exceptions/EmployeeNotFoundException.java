package com.Ems.EmployeeManagmentSystem.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmployeeNotFoundException extends RuntimeException{
    private final HttpStatus status;
    private final String errorCode;

    public EmployeeNotFoundException(String message, String errorCode) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = errorCode;
    }

    public EmployeeNotFoundException(String message){
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = "EmployeeNotFoundException";
    }

}
