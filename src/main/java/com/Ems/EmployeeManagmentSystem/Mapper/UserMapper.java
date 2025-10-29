package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "employee", ignore = true)
    Users toEntity(UserRequestDTO dto);

    UserResponseDTO toDto(Users entity);

}
