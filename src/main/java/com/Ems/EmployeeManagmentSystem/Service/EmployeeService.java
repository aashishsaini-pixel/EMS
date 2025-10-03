package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;

public interface EmployeeService {

    EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO);
    EmployeeResponseDTO deleteEmployee(Long id);
    Page<EmployeeResponseDTO> getEmployee(String name,
                                          EmployeeStatus status,
                                          String department,
                                          Boolean isActive,
                                          int page,
                                          int size,
                                          String sortBy);
    EmployeeResponseDTO updateEmployee(Long id , EmployeeRequestDTO employeeRequestDTO);
    EmployeeResponseDTO getLoggedInUser();
    void exportAllEmployeesPaginated(HttpServletResponse response) throws IOException;
}
