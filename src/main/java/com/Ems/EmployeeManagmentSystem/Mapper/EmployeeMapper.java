package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.dto.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeRequestDTO employeeRequestDTO);

    EmployeeResponseDTO toResponseDTO(Employee employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmployee(EmployeeRequestDTO employeeRequestDTO, @MappingTarget Employee employee);

}