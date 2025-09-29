package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.DTO.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "employee", ignore = true)
    Users toEntity(UserRequestDTO dto);

    UserResponseDTO toDto(Users entity);

    @Mapping(target = "employee", ignore = true)
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget Users entity);
}
