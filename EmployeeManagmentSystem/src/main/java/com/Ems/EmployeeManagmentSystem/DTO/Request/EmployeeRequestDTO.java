package com.Ems.EmployeeManagmentSystem.DTO.Request;

import com.Ems.EmployeeManagmentSystem.Validator.ValidEmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {

    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE , message = "Please enter Valid email")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @ValidEmployeeStatus
    private String status;

    @NotNull(message = "Date of joining is required")
    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate dateOfJoining;

}
