package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeAlreadyExistsException;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeNotFoundException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserNotFoundException;
import com.Ems.EmployeeManagmentSystem.Mapper.EmployeeMapper;
import com.Ems.EmployeeManagmentSystem.Repository.EmployeeRepository;
import com.Ems.EmployeeManagmentSystem.Repository.UsersRepository;
import com.Ems.EmployeeManagmentSystem.dto.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.EmployeeResponseDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Users user;
    private EmployeeRequestDTO requestDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeMapper, usersRepository);

        user = new Users();
        user.setId(1L);
        user.setEmail("test@user.com");

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("emp@test.com");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("IT");
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setIsActive(true);
        employee.setIsDeleted(false);
        employee.setUser(user);
        employee.setDateOfJoining(LocalDate.now());

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmail("emp@test.com");

        responseDTO = new EmployeeResponseDTO();
        responseDTO.setEmail("emp@test.com");
    }

    @Test
    void testAddEmployeeSuccess() {
        mockSecurityContext(user);
        when(usersRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(employeeRepository.existsByEmail("emp@test.com")).thenReturn(false);
        when(employeeMapper.toEntity(requestDTO)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            if (e.getId() == null) e.setId(1L);
            return e;
        });
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.addEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("emp@test.com", result.getEmail());
        verify(employeeRepository, times(2)).save(any(Employee.class));
    }

    @Test
    void testAddEmployee_UserNotFound() {
        mockSecurityContext(user);
        when(usersRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> employeeService.addEmployee(requestDTO));
    }

    @Test
    void testAddEmployee_EmployeeAlreadyExists() {
        mockSecurityContext(user);
        when(usersRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(employeeRepository.existsByEmail("emp@test.com")).thenReturn(true);
        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.addEmployee(requestDTO));
    }

    @Test
    void testAddEmployee_UnexpectedError() {
        mockSecurityContext(user);
        when(usersRepository.findById(user.getId())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> employeeService.addEmployee(requestDTO));
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.deleteEmployee(1L);

        assertEquals("emp@test.com", result.getEmail());
        assertTrue(employee.getIsDeleted());
        assertFalse(employee.getIsActive());
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void testGetEmployee_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findByFilters(any(), any(), any(), any(), any())).thenReturn(page);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        Page<EmployeeResponseDTO> result = employeeService.getEmployee("John", EmployeeStatus.ACTIVE, "IT", true, 0, 10, "id");

        assertEquals(1, result.getTotalElements());
        assertEquals("emp@test.com", result.getContent().get(0).getEmail());
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("emp@test.com", 1L)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, requestDTO);

        assertEquals("emp@test.com", result.getEmail());
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(1L, requestDTO));
    }

    @Test
    void testUpdateEmployee_InactiveOrDeleted() {
        employee.setIsActive(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(IllegalStateException.class, () -> employeeService.updateEmployee(1L, requestDTO));
    }

    @Test
    void testUpdateEmployee_EmailExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("emp@test.com", 1L)).thenReturn(true);

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.updateEmployee(1L, requestDTO));
    }

    @Test
    void testGetLoggedInUser_Success() {
        mockSecurityContext(user);
        user.setEmployee(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);
        EmployeeResponseDTO result = employeeService.getLoggedInUser();
        assertEquals("emp@test.com", result.getEmail());
    }

    @Test
    void testGetLoggedInUser_NotFound() {
        clearSecurityContext();
        assertThrows(NullPointerException.class, () -> employeeService.getLoggedInUser());
    }

    @Test
    void testGetLoggedInUser_ExceptionInMapping() {
        mockSecurityContext(user);
        user.setEmployee(employee);
        when(employeeMapper.toResponseDTO(employee)).thenThrow(new RuntimeException("Mapping error"));
        assertThrows(RuntimeException.class, () -> employeeService.getLoggedInUser());
    }

    @Test
    void testExportAllEmployeesPaginated_IOException() throws Exception {
        when(response.getOutputStream()).thenThrow(new IOException("Stream error"));
        assertThrows(IOException.class, () -> employeeService.exportAllEmployeesPaginated(response));
    }

    @Test
    void testGetUserNotCustomUserDetails() {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(context);
        Optional<Users> result = employeeService.getUser();
        assertTrue(result.isEmpty());
    }

    private void mockSecurityContext(Users user) {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));
        SecurityContextHolder.setContext(context);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void exportAllEmployeesPaginated_success() throws Exception {
        ReflectionTestUtils.setField(employeeService, "BATCH_SIZE", 2);

        employee.setEmployeeCode("EMP-2025-000001");
        employee.setEmail("john.doe@test.com");

        Page<Employee> page1 = new PageImpl<>(List.of(employee), PageRequest.of(0, 2), 3);
        Page<Employee> page2 = new PageImpl<>(List.of(employee), PageRequest.of(1, 2), 3);

        when(employeeRepository.findAll(any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        MockHttpServletResponse response = new MockHttpServletResponse();

        employeeService.exportAllEmployeesPaginated(response);

        String output = response.getContentAsString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Employee Code"), "Header should be present");
        assertTrue(output.contains("EMP-2025-000001"), "Employee code should be exported");
        assertTrue(output.contains("john.doe@test.com"), "Employee email should be exported");

        verify(employeeRepository, atLeast(2)).findAll(any(Pageable.class));
    }

    @Test
    void testDeleteEmployee_UnexpectedError() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void testUpdateEmployee_SaveThrowsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("emp@test.com", 1L)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Save failed"));
        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, requestDTO));
    }

    @Test
    void testExportAllEmployeesPaginated_UnexpectedError() throws Exception {
        ReflectionTestUtils.setField(employeeService, "BATCH_SIZE", 1);
        when(employeeRepository.findAll(any(Pageable.class)))
                .thenThrow(new RuntimeException("Unexpected error"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThrows(RuntimeException.class, () -> employeeService.exportAllEmployeesPaginated(response));
    }

    @Test
    void testExportAllEmployeesPaginated_SinglePage() throws Exception {
        ReflectionTestUtils.setField(employeeService, "BATCH_SIZE", 10);

        Page<Employee> page = new PageImpl<>(List.of(employee), PageRequest.of(0, 10), 1);
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = new MockHttpServletResponse();
        employeeService.exportAllEmployeesPaginated(response);

        String output = response.getContentAsString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Employee Code")); // header must exist
    }


}
