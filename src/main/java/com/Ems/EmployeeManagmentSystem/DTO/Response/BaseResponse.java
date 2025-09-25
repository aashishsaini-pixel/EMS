package com.Ems.EmployeeManagmentSystem.DTO.Response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

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
