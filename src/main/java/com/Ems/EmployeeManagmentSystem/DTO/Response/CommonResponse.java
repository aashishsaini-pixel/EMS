package com.Ems.EmployeeManagmentSystem.DTO.Response;

import com.Ems.EmployeeManagmentSystem.Enum.CommonResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private final Boolean isAuthenticated;
    private final CommonResponseStatus status;
    private final String message;
    private final String errorCode;
    private final T data;
    private final Map<String, String> errors;

    public static <T> CommonResponse<T> success(Boolean isAuthenticated, String message, T data) {
        return new CommonResponse<>(isAuthenticated, CommonResponseStatus.SUCCESS, message, null, data, null);
    }

    public static <T> CommonResponse<T> error(Boolean isAuthenticated, String message, String errorCode) {
        return new CommonResponse<>(isAuthenticated, CommonResponseStatus.ERROR, message, errorCode, null, null);
    }

    public static <T> CommonResponse<T> failed(Boolean isAuthenticated, String message, String errorCode, T data) {
        return new CommonResponse<>(isAuthenticated, CommonResponseStatus.FAILED, message, errorCode, data, null);
    }

    public static <T> CommonResponse<T> failed(Boolean isAuthenticated, String message, String errorCode) {
        return new CommonResponse<>(isAuthenticated, CommonResponseStatus.FAILED, message, errorCode,null , null);
    }



    public static CommonResponse<?> validationError(Map<String, String> fieldErrors) {
        return new CommonResponse<>(false, CommonResponseStatus.ERROR, "Validation failed", "VALIDATION_ERROR", null, fieldErrors);
    }
}
