package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.Enum.Role;
import com.Ems.EmployeeManagmentSystem.Repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("securePassword");
        testUser.setIsActive(true);
        testUser.setIsDeleted(false);
        testUser.setRole(Role.ADMIN);
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(usersRepository.findByEmailAndIsActiveTrueAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("securePassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(usersRepository, times(1))
                .findByEmailAndIsActiveTrueAndIsDeletedFalse("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(usersRepository.findByEmailAndIsActiveTrueAndIsDeletedFalse("notfound@example.com"))
                .thenReturn(Optional.empty());

        // Act + Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername("notfound@example.com")
        );

        assertTrue(exception.getMessage().contains("Username not found with email notfound@example.com"));

        verify(usersRepository, times(1))
                .findByEmailAndIsActiveTrueAndIsDeletedFalse("notfound@example.com");
    }
}
