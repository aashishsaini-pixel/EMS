package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.Enum.Role;
import com.Ems.EmployeeManagmentSystem.Exceptions.AuthenticationFailedException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserAlreadyExistsException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserNotFoundException;
import com.Ems.EmployeeManagmentSystem.Mapper.UserMapper;
import com.Ems.EmployeeManagmentSystem.Repository.UsersRepository;
import com.Ems.EmployeeManagmentSystem.dto.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Complete Test Suite")
class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO userRequestDTO;
    private Users user;
    private UserResponseDTO userResponseDTO;

    private static class TestServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream baos;

        public TestServletOutputStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "BATCH_SIZE", 1000);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setPassword("plainPass");
        userRequestDTO.setRole("ROLE_USER");

        user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPass");
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        when(usersRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenAnswer(inv -> {
            Users newUser = new Users();
            newUser.setEmail("test@example.com");
            newUser.setPassword("plainPass");
            newUser.setRole(Role.USER);
            return newUser;
        });

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(userMapper.toDto(any(Users.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());

        verify(usersRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("plainPass");
        verify(usersRepository).save(any(Users.class));
        verify(userMapper).toDto(any(Users.class));
    }

    @Test
    @DisplayName("Should throw exception when user already exists with same role")
    void testCreateUser_UserAlreadyExistsSameRoleThrows() {
        when(usersRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.of(user));
        user.setRole(Role.USER);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userRequestDTO));
        verify(usersRepository).findByEmail(userRequestDTO.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user already exists with different role")
    void testCreateUser_UserAlreadyExistsDifferentRoleThrows() {
        Users existingUser = new Users();
        existingUser.setEmail("test@example.com");
        existingUser.setRole(Role.ADMIN);

        when(usersRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userRequestDTO));
        verify(usersRepository).findByEmail(userRequestDTO.getEmail());
    }

    @Test
    @DisplayName("Should set isActive and isDeleted flags correctly on user creation")
    void testCreateUser_SetsCorrectFlags() {
        when(usersRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());

        Users newUser = new Users();
        newUser.setEmail("test@example.com");
        newUser.setPassword("plainPass");
        newUser.setRole(Role.USER);

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(newUser);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(userMapper.toDto(any(Users.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        verify(usersRepository).save(argThat(u ->
                u.getIsActive() == true && u.getIsDeleted() == false
        ));
    }

    @Test
    @DisplayName("Should encode password correctly during user creation")
    void testCreateUser_EncodesPassword() {
        when(usersRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());

        Users newUser = new Users();
        newUser.setEmail("test@example.com");
        newUser.setPassword("plainPass");

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(newUser);
        when(passwordEncoder.encode("plainPass")).thenReturn("super-encoded-password");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(userMapper.toDto(any(Users.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        verify(passwordEncoder).encode("plainPass");
        verify(usersRepository).save(argThat(u ->
                "super-encoded-password".equals(u.getPassword())
        ));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLogin_Success() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@example.com");
        loginRequestDTO.setPassword("plainPass");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        User springUser = new User("test@example.com", "encodedPass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(springUser);
        when(jwtService.generateToken(springUser)).thenReturn("jwt-token");

        JwtResponse jwtResponse = userService.login(loginRequestDTO);

        assertNotNull(jwtResponse);
        assertEquals("jwt-token", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("test@example.com", jwtResponse.getEmail());
    }

    @Test
    @DisplayName("Should throw AuthenticationFailedException when authentication fails")
    void testLoginAuthenticationFailsThrows() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("fail@example.com");
        loginRequestDTO.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("bad creds") {});

        assertThrows(AuthenticationFailedException.class, () -> userService.login(loginRequestDTO));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication returns false")
    void testLogin_NotAuthenticated_ThrowsBadCredentials() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("notauth@example.com");
        loginRequestDTO.setPassword("wrong");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> userService.login(loginRequestDTO));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).isAuthenticated();
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should generate token successfully on valid login")
    void testLogin_ValidCredentials_GeneratesToken() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("valid@example.com");
        loginRequestDTO.setPassword("validPass");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        User springUser = new User("valid@example.com", "encodedPass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(springUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token-12345");

        JwtResponse jwtResponse = userService.login(loginRequestDTO);

        assertNotNull(jwtResponse);
        assertEquals("jwt-token-12345", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("valid@example.com", jwtResponse.getEmail());
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should export users CSV successfully with single page")
    void testExportUsersPaginated_SinglePage_Success() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestServletOutputStream outputStream = new TestServletOutputStream(baos);

        when(response.getOutputStream()).thenReturn(outputStream);

        List<Users> usersList = new ArrayList<>();
        usersList.add(user);

        Page<Users> page = new PageImpl<>(usersList, PageRequest.of(0, 1000), 1);
        when(usersRepository.findAll(any(Pageable.class))).thenReturn(page);

        userService.exportUsersPaginated(response);

        verify(response).setContentType("text/csv; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(usersRepository).findAll(any(Pageable.class));

        String csvContent = baos.toString(StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("User ID"));
        assertTrue(csvContent.contains("test@example.com"));
    }

    @Test
    @DisplayName("Should export users CSV with multiple pages")
    void testExportUsersPaginated_MultiplePages_Success() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestServletOutputStream outputStream = new TestServletOutputStream(baos);

        when(response.getOutputStream()).thenReturn(outputStream);

        Users user1 = new Users();
        user1.setId(1L);
        user1.setEmail("user1@company.com");
        user1.setRole(Role.USER);
        user1.setIsActive(true);
        user1.setIsDeleted(false);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        Users user2 = new Users();
        user2.setId(2L);
        user2.setEmail("user2@company.com");
        user2.setRole(Role.ADMIN);
        user2.setIsActive(false);
        user2.setIsDeleted(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        List<Users> page1Users = List.of(user1);
        List<Users> page2Users = List.of(user2);

        Page<Users> page1 = new PageImpl<>(page1Users, PageRequest.of(0, 1000), 2);
        Page<Users> page2 = new PageImpl<>(page2Users, PageRequest.of(1, 1000), 2);

        when(usersRepository.findAll(any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        userService.exportUsersPaginated(response);
        verify(usersRepository, times(1)).findAll(any(Pageable.class));

        String csvContent = baos.toString(StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("user1@company.com"));
        assertFalse(csvContent.contains("user2@company.com"));
    }

    @Test
    @DisplayName("Should handle users with null fields in CSV export")
    void testExportUsersPaginated_WithNullFields() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestServletOutputStream outputStream = new TestServletOutputStream(baos);

        when(response.getOutputStream()).thenReturn(outputStream);

        Users userWithNulls = new Users();
        userWithNulls.setId(null);
        userWithNulls.setEmail(null);
        userWithNulls.setRole(null);
        userWithNulls.setIsActive(null);
        userWithNulls.setIsDeleted(null);
        userWithNulls.setCreatedAt(null);
        userWithNulls.setUpdatedAt(null);

        List<Users> usersList = List.of(userWithNulls);
        Page<Users> page = new PageImpl<>(usersList, PageRequest.of(0, 1000), 1);

        when(usersRepository.findAll(any(Pageable.class))).thenReturn(page);

        userService.exportUsersPaginated(response);

        verify(usersRepository).findAll(any(Pageable.class));

        String csvContent = baos.toString(StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("User ID"));
        assertTrue(csvContent.contains(",,"));
    }

    @Test
    @DisplayName("Should handle empty user list in CSV export")
    void testExportUsersPaginated_EmptyList() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestServletOutputStream outputStream = new TestServletOutputStream(baos);

        when(response.getOutputStream()).thenReturn(outputStream);

        List<Users> emptyList = Collections.emptyList();
        Page<Users> emptyPage = new PageImpl<>(emptyList, PageRequest.of(0, 1000), 0);

        when(usersRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        userService.exportUsersPaginated(response);

        verify(usersRepository).findAll(any(Pageable.class));

        String csvContent = baos.toString(StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("User ID"));
        assertFalse(csvContent.contains("@"));
    }

    @Test
    @DisplayName("Should throw IOException when getOutputStream fails")
    void testExportUsersPaginated_IOException() throws Exception {
        when(response.getOutputStream()).thenThrow(new IOException("Stream error"));

        assertThrows(IOException.class, () -> userService.exportUsersPaginated(response));
    }

    @Test
    @DisplayName("Should handle RuntimeException during CSV export")
    void testExportUsersPaginated_RuntimeException() throws Exception {
        when(response.getOutputStream()).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.exportUsersPaginated(response));

        assertTrue(exception.getMessage().contains("Failed to export users to CSV"));
        verify(response).getOutputStream();
    }

//    @Test
//    @DisplayName("Should use configured BATCH_SIZE for pagination")
//    void testExportUsersPaginated_UsesConfiguredBatchSize() throws Exception {
//        // Given
//        ReflectionTestUtils.setField(userService, "BATCH_SIZE", 500);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        TestServletOutputStream outputStream = new TestServletOutputStream(baos);
//
//        when(response.getOutputStream()).thenReturn(outputStream);
//
//        List<Users> usersList = List.of(user);
//        Page<Users> page = new PageImpl<>(usersList, PageRequest.of(0, 500), 1);
//
//        when(usersRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        // When
//        userService.exportUsersPaginated(response);
//
//        // Then
//        verify(usersRepository).findAll(argThat(pageable ->
//                pageable.getPageSize() == 500
//        ));
//    }

    // ============================================
    // DELETE USER TESTS
    // ============================================

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.deleteUser(1L);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertTrue(user.getIsDeleted());
        assertFalse(user.getIsActive());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void testDeleteUser_NotFoundThrows() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    @DisplayName("Should throw RuntimeException when save fails")
    void testDeleteUser_SaveFailsThrows() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any())).thenThrow(new RuntimeException("db fail"));

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("Should delete user with employee successfully")
    void testDeleteUser_WithEmployee() {
        com.Ems.EmployeeManagmentSystem.Entity.Employee employeeEntity =
                new com.Ems.EmployeeManagmentSystem.Entity.Employee();
        employeeEntity.setId(2L);
        employeeEntity.setIsActive(true);
        employeeEntity.setIsDeleted(false);

        user.setEmployee(employeeEntity);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toDto(any(Users.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.deleteUser(1L);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertTrue(user.getIsDeleted());
        assertFalse(user.getIsActive());
        assertNotNull(user.getEmployee());
        assertTrue(user.getEmployee().getIsDeleted());
        assertFalse(user.getEmployee().getIsActive());

        verify(usersRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verify(userMapper).toDto(any(Users.class));
    }

    @Test
    @DisplayName("Should delete user without employee successfully")
    void testDeleteUser_WithoutEmployee_Success() {
        user.setEmployee(null);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(userMapper.toDto(any(Users.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.deleteUser(1L);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertTrue(user.getIsDeleted());
        assertFalse(user.getIsActive());
        assertNull(user.getEmployee());

        verify(usersRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verify(userMapper).toDto(any(Users.class));
    }

    @Test
    @DisplayName("Should handle exception during user deletion and wrap in RuntimeException")
    void testDeleteUser_ExceptionDuringSave_WrappedInRuntimeException() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(1L));

        assertTrue(exception.getMessage().contains("Failed to delete user with ID: 1"));
        assertTrue(exception.getCause().getMessage().contains("Database connection error"));

        verify(usersRepository).findById(1L);
        verify(usersRepository).save(any(Users.class));
        verifyNoInteractions(userMapper);
    }
}