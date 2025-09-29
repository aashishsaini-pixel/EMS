package com.Ems.EmployeeManagmentSystem.DTO.Response;

import com.Ems.EmployeeManagmentSystem.Enum.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String role;
    private Boolean isActive;
}
