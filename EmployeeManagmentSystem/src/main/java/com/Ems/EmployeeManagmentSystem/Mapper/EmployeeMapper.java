package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeRequestDTO employeeRequestDTO);

    EmployeeResponseDTO toResponseDTO(Employee employee);

}