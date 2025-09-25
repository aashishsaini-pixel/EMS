package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface EmployeeService {

    EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO);
//    EmployeeResponseDTO updateEmployee(int id , EmployeeRequestDTO employeeRequestDTO);
    EmployeeResponseDTO deleteEmployee(Long id);
    Page<EmployeeResponseDTO> getEmployee(String name,
                                          EmployeeStatus status,
                                          String department,
                                          Boolean isActive,
                                          int page,
                                          int size,
                                          String sortBy);

}
