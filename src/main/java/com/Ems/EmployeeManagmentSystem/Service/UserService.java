package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.dto.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    JwtResponse login(LoginRequestDTO loginRequestDTO);

    UserResponseDTO deleteUser(Long id);

    void exportUsersPaginated(HttpServletResponse response) throws IOException;
}
