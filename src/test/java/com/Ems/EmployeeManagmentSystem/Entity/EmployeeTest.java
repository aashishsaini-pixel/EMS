package com.Ems.EmployeeManagmentSystem.Entity;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit test cases for the employee entity")
class EmployeeTest {

    private Employee employee;

    @BeforeEach
    @DisplayName("Setting the user and employee object before each method")
    void setUp() {
        Users user = Users.builder()
                .id(1L)
                .email("test@example.com")
                .password("pass")
                .role(null)
                .build();

        employee = new Employee(
                1L,
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                "IT",
                EmployeeStatus.ACTIVE,
                LocalDate.of(2022, 1, 1),
                true,
                false,
                user
        );
    }

    @Test
    @DisplayName("Testing the getters of the Employee Entity")
    void testGetters() {
        assertEquals(1L, employee.getId());
        assertEquals("EMP001", employee.getEmployeeCode());
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals("IT", employee.getDepartment());
        assertEquals(EmployeeStatus.ACTIVE, employee.getStatus());
        assertEquals(LocalDate.of(2022, 1, 1), employee.getDateOfJoining());
        assertTrue(employee.getIsActive());
        assertFalse(employee.getIsDeleted());
        assertNotNull(employee.getUser());
        assertEquals("test@example.com", employee.getUser().getEmail());
    }

    @Test
    @DisplayName("Testing the setters of the employee entity")
    void testSetters() {
        employee.setFirstName("Jane");
        employee.setIsActive(false);

        assertEquals("Jane", employee.getFirstName());
        assertFalse(employee.getIsActive());
    }
}
