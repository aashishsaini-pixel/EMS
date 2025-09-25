package com.Ems.EmployeeManagmentSystem.Validator;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EmployeeStatusValidator implements ConstraintValidator<ValidEmployeeStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Arrays.stream(EmployeeStatus.values())
                     .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
