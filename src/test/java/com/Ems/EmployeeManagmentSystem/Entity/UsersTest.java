package com.Ems.EmployeeManagmentSystem.Entity;

import com.Ems.EmployeeManagmentSystem.Enum.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit test cases for the user entity")
class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .email("admin@example.com")
                .password("securepassword")
                .role(Role.ADMIN)
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("Testing the user fields")
    void testUserFields() {
        assertEquals(1L, user.getId());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("securepassword", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.getIsActive());
        assertFalse(user.getIsDeleted());
    }

    @Test
    @DisplayName("Testing the setters in the user")
    void testUserSetters() {
        user.setEmail("new@example.com");
        user.setIsDeleted(true);
        user.setIsActive(true);
        user.setRole(Role.USER);
        assertEquals("new@example.com", user.getEmail());
        assertTrue(user.getIsDeleted());
    }
}
