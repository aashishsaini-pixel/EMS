package com.Ems.EmployeeManagmentSystem.dto.request;

import com.Ems.EmployeeManagmentSystem.dto.Request.LoginRequestDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private LoginRequestDTO createValidDto() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test.user@example.com");
        dto.setPassword("StrongP@ssw0rd");
        return dto;
    }

    @Test
    @DisplayName("Test With no violations")
    void testValidDtoWithNoViolations() {
        LoginRequestDTO dto = createValidDto();
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    @DisplayName("Test with blank email")
    void testValidDtoWithBlankEmail() {
        LoginRequestDTO dto = createValidDto();
        dto.setEmail(" ");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Test when the email format is invalid")
    void testValidDtoWithInvalidEmail() {
        LoginRequestDTO dto = createValidDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Test dto with Short password")
    void testValidDtoWithShortPassword() {
        LoginRequestDTO dto = createValidDto();
        dto.setPassword("Abc1@");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Test dto with Failed regex")
    void testValidDtoWithFailedRegex() {
        LoginRequestDTO dto = createValidDto();
        dto.setPassword("password123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Test dot with multiple violations")
    void testMultipleViolationsTogether() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("bademail");
        dto.setPassword("1234566781");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
