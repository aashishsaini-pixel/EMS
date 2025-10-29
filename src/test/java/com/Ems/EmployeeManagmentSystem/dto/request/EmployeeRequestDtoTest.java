package com.Ems.EmployeeManagmentSystem.dto.request;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.dto.Request.EmployeeRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private EmployeeRequestDTO validDto(){
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice.smith@example.com");
        dto.setDepartment("Engineering");
        dto.setStatus(EmployeeStatus.ACTIVE.toString());
        dto.setDateOfJoining(LocalDate.now().minusDays(1));
        return dto;
    }

    @Test
    @DisplayName("Valid dto with no Voilations")
    void testValidDtoWithNoVoilations(){
        EmployeeRequestDTO dto = validDto();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty() , "No violations found");
    }

    @Test
    @DisplayName("First Name is Blank")
    void testFirstNameIsBlank(){
        EmployeeRequestDTO dto = validDto();
        dto.setFirstName(" ");
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty() , "No violations found");
        assertTrue(violations.stream().anyMatch( v -> v.getPropertyPath().toString().equals("firstName") ));
     }

     @Test
     @DisplayName("Last Name is Blank")
     void testLastNameIsBlank(){
        EmployeeRequestDTO dto = validDto();
        dto.setLastName(" ");
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty() , "No violations found");
        assertTrue(violations.stream().anyMatch( v -> v.getPropertyPath().toString().equals("lastName") ));
     }

    @Test
    @DisplayName("Test invalid email format")
    void testInvalidEmailFormat() {
        EmployeeRequestDTO dto = validDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Test for the Future date of joining")
    void testFutureDateOfJoining() {
        EmployeeRequestDTO dto = validDto();
        dto.setDateOfJoining(LocalDate.now().plusDays(5));

        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateOfJoining")));
    }

    @Test
    void testMultipleViolations_shouldReportAllViolations() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName("");                     // @NotBlank
        dto.setLastName("");                      // @NotBlank
        dto.setEmail("not-an-email");             // @Email
        dto.setDepartment("");                    // @NotBlank
        dto.setStatus("INVALID_STATUS");          // Custom validator
        dto.setDateOfJoining(LocalDate.now().plusDays(5)); // @PastOrPresent

        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);

        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertEquals(6, violations.size(), "Expected 6 validation errors");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("department")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateOfJoining")));
    }


}
