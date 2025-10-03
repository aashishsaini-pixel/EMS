package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    JwtResponse login(LoginRequestDTO loginRequestDTO);

    UserResponseDTO deleteUser(Long id);

    void exportUsersPaginated(HttpServletResponse response) throws IOException;
}
