package com.Ems.EmployeeManagmentSystem.dto.response;

import com.Ems.EmployeeManagmentSystem.dto.Response.EmployeeResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeResponseDTOTest {

    @Test
    @DisplayName("Should create object using all-args constructor")
    void shouldCreateObjectUsingAllArgsConstructor() {
        LocalDate dateOfJoining = LocalDate.of(2022, 1, 1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        LocalDateTime updatedAt = LocalDateTime.now();

        EmployeeResponseDTO dto = new EmployeeResponseDTO(
                1L,
                "EMP123",
                "John",
                "Doe",
                "john.doe@example.com",
                "IT",
                "ACTIVE",
                dateOfJoining,
                true,
                createdAt,
                updatedAt
        );

        assertEquals(1L, dto.getId());
        assertEquals("EMP123", dto.getEmployeeCode());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("IT", dto.getDepartment());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals(dateOfJoining, dto.getDateOfJoining());
        assertTrue(dto.getIsActive());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get fields using setters and getters")
    void shouldSetAndGetFields() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        LocalDate dateOfJoining = LocalDate.of(2023, 5, 10);
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 6, 1, 12, 30);

        dto.setId(2L);
        dto.setEmployeeCode("EMP456");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setDepartment("HR");
        dto.setStatus("INACTIVE");
        dto.setDateOfJoining(dateOfJoining);
        dto.setIsActive(false);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);

        assertEquals(2L, dto.getId());
        assertEquals("EMP456", dto.getEmployeeCode());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("jane.smith@example.com", dto.getEmail());
        assertEquals("HR", dto.getDepartment());
        assertEquals("INACTIVE", dto.getStatus());
        assertEquals(dateOfJoining, dto.getDateOfJoining());
        assertFalse(dto.getIsActive());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }
}
