package com.Ems.EmployeeManagmentSystem.dto.request;

import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRequestDTO createValidDto() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("StrongP@ss1");
        dto.setRole("ADMIN");
        return dto;
    }

    @Test
    @DisplayName("Should pass validation for valid email and strong password")
    void shouldPassValidationForValidDto() {
        UserRequestDTO dto = createValidDto();

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void shouldFailWhenEmailIsBlank() {
        UserRequestDTO dto = createValidDto();
        dto.setEmail(" ");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void shouldFailWhenEmailFormatIsInvalid() {
        UserRequestDTO dto = createValidDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail when email exceeds max length")
    void shouldFailWhenEmailExceedsMaxLength() {
        UserRequestDTO dto = createValidDto();
        dto.setEmail("a".repeat(101) + "@example.com");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail when password is blank")
    void shouldFailWhenPasswordIsBlank() {
        UserRequestDTO dto = createValidDto();
        dto.setPassword(" ");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail when password is too short")
    void shouldFailWhenPasswordIsTooShort() {
        UserRequestDTO dto = createValidDto();
        dto.setPassword("P@ss1"); // Too short

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail when password does not match strength requirements")
    void shouldFailWhenPasswordIsWeak() {
        UserRequestDTO dto = createValidDto();
        dto.setPassword("weakpassword");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail with multiple violations when both email and password are invalid")
    void shouldFailWithMultipleViolations() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("bademail");
        dto.setPassword("123");
        dto.setRole("USER");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should pass when role is null or not set")
    void shouldPassWhenRoleIsNull() {
        UserRequestDTO dto = createValidDto();
        dto.setRole(null);

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no violations since role has no constraints");
    }
}
