package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.Exceptions.UserNotFoundException;
import com.Ems.EmployeeManagmentSystem.dto.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.dto.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.dto.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserRequestDTO validUserRequest;
    private UserResponseDTO userResponseDTO;
    private LoginRequestDTO loginRequestDTO;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        // Setup valid UserRequestDTO
        validUserRequest = UserRequestDTO.builder()
                .email("john.doe@example.com")
                .password("Password123@")
                .role("USER")
                .build();

        // Setup UserResponseDTO
        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("john.doe@example.com")
                .role("USER")
                .isActive(true)
                .build();

        // Setup LoginRequestDTO
        loginRequestDTO = LoginRequestDTO.builder()
                .email("john.doe@example.com")
                .password("Password123@")
                .build();

        // Setup JwtResponse
        jwtResponse = JwtResponse.builder()
                .token("jwt-token-here")
                .tokenType("Bearer")
                .email("john.doe@example.com")
                .build();
    }

    // ===========================================
    // USER REGISTRATION TESTS
    // ===========================================

    @Test
    @DisplayName("POST /users/register - Should register user successfully with valid input")
    void createUser_WithValidInput_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User Created Successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.isActive").value(true));

        // Verify
        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when email is blank")
    void createUser_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email("")
                .password("Password123@")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when email is invalid")
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email("invalid-email")
                .password("Password123@")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when password is too short")
    void createUser_WithShortPassword_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email("john.doe@example.com")
                .password("Short1@")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when password lacks uppercase")
    void createUser_WithPasswordNoUppercase_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email("john.doe@example.com")
                .password("password123@")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when password lacks special character")
    void createUser_WithPasswordNoSpecialChar_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email("john.doe@example.com")
                .password("Password123")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should return bad request when email exceeds max length")
    void createUser_WithLongEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String longEmail = "a".repeat(90) + "@example.com";
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .email(longEmail)
                .password("Password123@")
                .role("USER")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    // ===========================================
    // USER LOGIN TESTS
    // ===========================================

    @Test
    @DisplayName("POST /users/login - Should login user successfully with valid credentials")
    void loginUser_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
        // Arrange
        when(userService.login(any(LoginRequestDTO.class))).thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Logged in Successfully."))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));

        verify(userService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should return bad request when login email is blank")
    void loginUser_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO invalidLoginRequest = LoginRequestDTO.builder()
                .email("")
                .password("Password123@")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should return bad request when login email is invalid")
    void loginUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO invalidLoginRequest = LoginRequestDTO.builder()
                .email("invalid-email")
                .password("Password123@")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should return bad request when login password is blank")
    void loginUser_WithBlankPassword_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO invalidLoginRequest = LoginRequestDTO.builder()
                .email("john.doe@example.com")
                .password("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should return bad request when login password is too short")
    void loginUser_WithShortPassword_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO invalidLoginRequest = LoginRequestDTO.builder()
                .email("john.doe@example.com")
                .password("Short1@")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    // ===========================================
    // USER DELETION TESTS
    // ===========================================

    @Test
    @DisplayName("DELETE /users/{id} - Should delete user successfully with valid ID")
    void deleteUser_WithValidId_ShouldReturnSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Deleted successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));

        verify(userService, times(1)).deleteUser(userId);
    }

//    @Test
//    @DisplayName("DELETE /users/{id} - Should handle service exceptions gracefully")
//    void deleteUser_WhenServiceThrowsException_ShouldHandleGracefully() throws Exception {
//        // Arrange
//        Long userId = 999L;
//        when(userService.deleteUser(userId))
//                .thenThrow(new UserNotFoundException("User not found"));
//
//        // Act & Assert
//        mockMvc.perform(delete("/users/{id}", userId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        verify(userService, times(1)).deleteUser(userId);
//    }


// ===========================================
// EDGE CASE TESTS
// ===========================================

    @Test
    @DisplayName("POST /users/register - Should handle empty request body")
    void createUser_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should handle empty request body")
    void loginUser_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should handle incomplete JSON")
    void createUser_WithIncompleteJson_ShouldReturnBadRequest() throws Exception {
        // Act & Assert - Valid JSON syntax but missing required fields
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}")) // Missing password
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/login - Should handle incomplete JSON")
    void loginUser_WithIncompleteJson_ShouldReturnBadRequest() throws Exception {
        // Act & Assert - Valid JSON syntax but missing required fields
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"Password123@\"}")) // Missing email
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users/register - Should handle wrong content type")
    void createUser_WithWrongContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }
}