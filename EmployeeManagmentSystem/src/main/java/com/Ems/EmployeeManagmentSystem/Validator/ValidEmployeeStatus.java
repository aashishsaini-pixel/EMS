package com.Ems.EmployeeManagmentSystem.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmployeeStatusValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmployeeStatus {
    String message() default "EmployeeStatus can be either ACTIVE or INACTIVE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
