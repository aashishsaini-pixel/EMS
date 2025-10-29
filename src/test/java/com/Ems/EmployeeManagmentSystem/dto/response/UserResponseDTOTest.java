package com.Ems.EmployeeManagmentSystem.dto.response;

import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    @DisplayName("Should create object using all-args constructor")
    void shouldCreateObjectUsingAllArgsConstructor() {
        UserResponseDTO dto = new UserResponseDTO(1L, "test@example.com", "ADMIN", true);

        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("Should create object using no-args constructor and set values")
    void shouldCreateObjectUsingNoArgsConstructorAndSetFields() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(2L);
        dto.setEmail("user@example.com");
        dto.setRole("USER");
        dto.setIsActive(false);

        assertEquals(2L, dto.getId());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("USER", dto.getRole());
        assertFalse(dto.getIsActive());
    }

    @Test
    @DisplayName("Should match values when using getters after setters")
    void shouldMatchValuesUsingGetters() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(3L);
        dto.setEmail("check@example.com");
        dto.setRole("USER");
        dto.setIsActive(true);

        assertAll(
                () -> assertEquals(3L, dto.getId()),
                () -> assertEquals("check@example.com", dto.getEmail()),
                () -> assertEquals("USER", dto.getRole()),
                () -> assertTrue(dto.getIsActive())
        );
    }
}
