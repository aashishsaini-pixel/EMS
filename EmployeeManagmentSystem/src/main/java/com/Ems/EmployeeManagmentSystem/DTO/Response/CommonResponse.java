package com.Ems.EmployeeManagmentSystem.DTO.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponse<T> {

    private final String status;
    private final String message;
    private final String errorCode;
    private final T data;


    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>("success", message, null, data);
    }

    public static <T> CommonResponse<T> error(String message, String errorCode, T data) {
        return new CommonResponse<>("error", message, errorCode, data);
    }

    public static CommonResponse<?> error(String message, String errorCode) {
        return error(message, errorCode, null);
    }
}
