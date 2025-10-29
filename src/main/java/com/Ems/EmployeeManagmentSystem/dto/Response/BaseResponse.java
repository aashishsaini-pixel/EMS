package com.Ems.EmployeeManagmentSystem.dto.Response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class BaseResponse<T> {

    Boolean isAuthenticated = false;
    String message;
    List<String> errors;
    Boolean isSuccess = false;
    T data;
    List<T> content;
}
