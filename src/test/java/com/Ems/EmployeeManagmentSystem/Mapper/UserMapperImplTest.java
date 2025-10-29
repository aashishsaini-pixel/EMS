package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToEntity_ValidDto() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("john@example.com");
        dto.setPassword("secret");

        Users user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertNull(user.getEmployee(), "Employee should be ignored");
    }

    @Test
    void testToEntity_NullDto() {
        Users user = userMapper.toEntity(null);
        assertNull(user);
    }

    @Test
    void testToDto_ValidEntity() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setPassword("encoded");
        user.setIsActive(true);

        UserResponseDTO dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("alice@example.com", dto.getEmail());
        assertTrue(dto.getIsActive());
    }

    @Test
    void testToDto_NullEntity() {
        UserResponseDTO dto = userMapper.toDto(null);
        assertNull(dto);
    }
}
