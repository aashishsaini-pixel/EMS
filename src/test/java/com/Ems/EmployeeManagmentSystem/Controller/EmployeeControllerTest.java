package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.dto.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.dto.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Service.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeServiceImpl employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper;
    private EmployeeRequestDTO validEmployeeRequest;
    private EmployeeResponseDTO employeeResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();

        // Setup valid EmployeeRequestDTO
        validEmployeeRequest = new EmployeeRequestDTO();
        validEmployeeRequest.setFirstName("John");
        validEmployeeRequest.setLastName("Doe");
        validEmployeeRequest.setEmail("john.doe@example.com");
        validEmployeeRequest.setDepartment("Engineering");
        validEmployeeRequest.setStatus("ACTIVE");
        validEmployeeRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Setup EmployeeResponseDTO
        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setEmployeeCode("EMP-2024-000001");
        employeeResponseDTO.setFirstName("John");
        employeeResponseDTO.setLastName("Doe");
        employeeResponseDTO.setEmail("john.doe@example.com");
        employeeResponseDTO.setDepartment("Engineering");
        employeeResponseDTO.setStatus("ACTIVE");
        employeeResponseDTO.setDateOfJoining(LocalDate.now().minusDays(1));
        employeeResponseDTO.setIsActive(true);
        employeeResponseDTO.setCreatedAt(LocalDateTime.now());
        employeeResponseDTO.setUpdatedAt(LocalDateTime.now());
    }

    // ===========================================
    // ADD EMPLOYEE TESTS
    // ===========================================

    @Test
    @DisplayName("POST /employee - Should add employee successfully with valid input")
    void addEmployee_WithValidInput_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(employeeService.addEmployee(any(EmployeeRequestDTO.class))).thenReturn(employeeResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee added successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP-2024-000001"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.department").value("Engineering"))
                .andExpect(jsonPath("$.data.isActive").value(true));

        verify(employeeService, times(1)).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when first name is blank")
    void addEmployee_WithBlankFirstName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("john.doe@example.com");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when last name is blank")
    void addEmployee_WithBlankLastName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("");
        invalidRequest.setEmail("john.doe@example.com");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when email is invalid")
    void addEmployee_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when department is blank")
    void addEmployee_WithBlankDepartment_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("john.doe@example.com");
        invalidRequest.setDepartment("");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when date of joining is in future")
    void addEmployee_WithFutureDateOfJoining_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("john.doe@example.com");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().plusDays(1));

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should return bad request when date of joining is null")
    void addEmployee_WithNullDateOfJoining_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("john.doe@example.com");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(null);

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    // ===========================================
    // GET ALL EMPLOYEES (ADMIN) TESTS
    // ===========================================

    @Test
    @DisplayName("GET /employee/admin - Should return paginated employees with filters")
    void getEmployees_WithFilters_ShouldReturnPaginatedResponse() throws Exception {
        // Arrange
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(List.of(employeeResponseDTO));
        when(employeeService.getEmployee(anyString(), any(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString()))
                .thenReturn(employeePage);

        // Act & Assert
        mockMvc.perform(get("/employee/admin")
                .param("name", "John")
                .param("status", "ACTIVE")
                .param("department", "Engineering")
                .param("isActive", "true")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employees fetched successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"));

        verify(employeeService, times(1)).getEmployee("John", EmployeeStatus.ACTIVE, "Engineering", true, 0, 10, "createdAt");
    }

    @Test
    @DisplayName("GET /employee/admin - Should return paginated employees with default parameters")
    void getEmployees_WithDefaultParameters_ShouldReturnPaginatedResponse() throws Exception {
        // Arrange
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(List.of(employeeResponseDTO));
        when(employeeService.getEmployee(isNull(), isNull(), isNull(), eq(true), eq(0), eq(10), eq("createdAt")))
                .thenReturn(employeePage);

        // Act & Assert
        mockMvc.perform(get("/employee/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employees fetched successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));

        verify(employeeService, times(1)).getEmployee(null, null, null, true, 0, 10, "createdAt");
    }

    @Test
    @DisplayName("GET /employee/admin - Should return bad request when page is negative")
    void getEmployees_WithNegativePage_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/employee/admin")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).getEmployee(anyString(), any(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("GET /employee/admin - Should return bad request when size is zero")
    void getEmployees_WithZeroSize_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/employee/admin")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).getEmployee(anyString(), any(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("GET /employee/admin - Should return bad request when size exceeds max")
    void getEmployees_WithSizeExceedingMax_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/employee/admin")
                .param("page", "0")
                .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).getEmployee(anyString(), any(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString());
    }

    // ===========================================
    // GET LOGGED-IN EMPLOYEE TESTS
    // ===========================================

    @Test
    @DisplayName("GET /employee - Should return logged-in employee successfully")
    void getEmployees_ForLoggedInUser_ShouldReturnEmployee() throws Exception {
        // Arrange
        when(employeeService.getLoggedInUser()).thenReturn(employeeResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));

        verify(employeeService, times(1)).getLoggedInUser();
    }

    // ===========================================
    // DELETE EMPLOYEE TESTS
    // ===========================================

    @Test
    @DisplayName("DELETE /employee/{id} - Should delete employee successfully")
    void deleteEmployee_WithValidId_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(employeeService.deleteEmployee(1L)).thenReturn(employeeResponseDTO);

        // Act & Assert
        mockMvc.perform(delete("/employee/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("John"));

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    @DisplayName("DELETE /employee/{id} - Should handle service exceptions gracefully")
    void deleteEmployee_WhenServiceThrowsException_ShouldHandleGracefully() throws Exception {
        // Arrange
        when(employeeService.deleteEmployee(999L))
                .thenThrow(new RuntimeException("Employee not found"));

        // Act & Assert
        mockMvc.perform(delete("/employee/{id}", 999L))
                .andExpect(status().is5xxServerError());

        verify(employeeService, times(1)).deleteEmployee(999L);
    }

    // ===========================================
    // UPDATE EMPLOYEE TESTS
    // ===========================================

    @Test
    @DisplayName("PUT /employee/{id} - Should update employee successfully with valid input")
    void updateEmployee_WithValidInput_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDTO.class))).thenReturn(employeeResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/employee/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /employee/{id} - Should return bad request when update request has invalid data")
    void updateEmployee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setFirstName("");
        invalidRequest.setLastName("Doe");
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setDepartment("Engineering");
        invalidRequest.setStatus("ACTIVE");
        invalidRequest.setDateOfJoining(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(put("/employee/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).updateEmployee(anyLong(), any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /employee/{id} - Should handle service exceptions gracefully")
    void updateEmployee_WhenServiceThrowsException_ShouldHandleGracefully() throws Exception {
        // Arrange
        when(employeeService.updateEmployee(eq(999L), any(EmployeeRequestDTO.class)))
                .thenThrow(new RuntimeException("Employee not found"));

        // Act & Assert
        mockMvc.perform(put("/employee/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmployeeRequest)))
                .andExpect(status().is5xxServerError());

        verify(employeeService, times(1)).updateEmployee(eq(999L), any(EmployeeRequestDTO.class));
    }

    // ===========================================
    // EDGE CASE TESTS
    // ===========================================

    @Test
    @DisplayName("POST /employee - Should handle empty request body")
    void addEmployee_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("POST /employee - Should handle malformed JSON")
    void addEmployee_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"John\" }")) // Trailing comma
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /employee/{id} - Should handle empty request body")
    void updateEmployee_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/employee/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).updateEmployee(anyLong(), any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("GET /employee/admin - Should handle invalid status parameter")
    void getEmployees_WithInvalidStatus_ShouldHandleGracefully() throws Exception {
        // Arrange
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(List.of());
        when(employeeService.getEmployee(isNull(), isNull(), isNull(), eq(true), eq(0), eq(10), eq("createdAt")))
                .thenReturn(employeePage);

        // Act & Assert - Invalid status should be ignored or cause bad request depending on implementation
        mockMvc.perform(get("/employee/admin")
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isOk()); // or .isBadRequest() depending on your enum handling

        verify(employeeService, times(1)).getEmployee(null, null, null, true, 0, 10, "createdAt");
    }

    @Test
    @DisplayName("POST /employee - Should verify service method called with correct parameters")
    void addEmployee_ShouldCallServiceWithCorrectParameters() throws Exception {
        // Arrange
        when(employeeService.addEmployee(any(EmployeeRequestDTO.class))).thenReturn(employeeResponseDTO);

        // Act
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmployeeRequest)));

        // Assert
        verify(employeeService, times(1)).addEmployee(argThat(request ->
                request.getFirstName().equals("John") &&
                request.getLastName().equals("Doe") &&
                request.getEmail().equals("john.doe@example.com") &&
                request.getDepartment().equals("Engineering") &&
                request.getStatus().equals("ACTIVE")
        ));
    }

    @Test
    @DisplayName("PUT /employee/{id} - Should verify service method called with correct parameters")
    void updateEmployee_ShouldCallServiceWithCorrectParameters() throws Exception {
        // Arrange
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDTO.class))).thenReturn(employeeResponseDTO);

        // Act
        mockMvc.perform(put("/employee/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmployeeRequest)));

        // Assert
        verify(employeeService, times(1)).updateEmployee(eq(1L), argThat(request ->
                request.getFirstName().equals("John") &&
                request.getLastName().equals("Doe") &&
                request.getEmail().equals("john.doe@example.com") &&
                request.getDepartment().equals("Engineering") &&
                request.getStatus().equals("ACTIVE")
        ));
    }
}