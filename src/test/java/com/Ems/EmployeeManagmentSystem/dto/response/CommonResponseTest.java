package com.Ems.EmployeeManagmentSystem.dto.response;

import com.Ems.EmployeeManagmentSystem.Enum.CommonResponseStatus;
import com.Ems.EmployeeManagmentSystem.dto.Response.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommonResponseTest {

    @Test
    @DisplayName("Should create success response with data")
    void shouldCreateSuccessResponse() {
        String data = "Sample Data";

        CommonResponse<String> response = CommonResponse.success(true, "Operation successful", data);

        assertTrue(response.getIsAuthenticated());
        assertEquals(CommonResponseStatus.SUCCESS, response.getStatus());
        assertEquals("Operation successful", response.getMessage());
        assertNull(response.getErrorCode());
        assertEquals("Sample Data", response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Should create error response without data or errors")
    void shouldCreateErrorResponse() {
        CommonResponse<String> response = CommonResponse.error(false, "Something went wrong", "ERR001");

        assertFalse(response.getIsAuthenticated());
        assertEquals(CommonResponseStatus.ERROR, response.getStatus());
        assertEquals("Something went wrong", response.getMessage());
        assertEquals("ERR001", response.getErrorCode());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Should create failed response with data")
    void shouldCreateFailedResponseWithData() {
        String data = "Partial failure";

        CommonResponse<String> response = CommonResponse.failed(true, "Failed with some issues", "FAIL001", data);

        assertTrue(response.getIsAuthenticated());
        assertEquals(CommonResponseStatus.FAILED, response.getStatus());
        assertEquals("Failed with some issues", response.getMessage());
        assertEquals("FAIL001", response.getErrorCode());
        assertEquals(data, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Should create failed response without data")
    void shouldCreateFailedResponseWithoutData() {
        CommonResponse<String> response = CommonResponse.failed(false, "Failed completely", "FAIL002");

        assertFalse(response.getIsAuthenticated());
        assertEquals(CommonResponseStatus.FAILED, response.getStatus());
        assertEquals("FAIL002", response.getErrorCode());
        assertEquals("Failed completely", response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Should create validation error response with field errors")
    void shouldCreateValidationErrorResponse() {
        Map<String, String> errors = Map.of(
                "email", "Invalid email format",
                "password", "Password is too weak"
        );

        CommonResponse<?> response = CommonResponse.validationError(errors);

        assertFalse(response.getIsAuthenticated());
        assertEquals(CommonResponseStatus.ERROR, response.getStatus());
        assertEquals("Validation failed", response.getMessage());
        assertEquals("VALIDATION_ERROR", response.getErrorCode());
        assertNull(response.getData());
        assertEquals(errors, response.getErrors());
    }
}
