package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Controller", description = "Endpoints related to user registration, login, and management.")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Registers a new user with the given details. No authentication required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<CommonResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
     log.info("Creating a user :)");
     UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
     log.info("User created with User response : {}", userResponseDTO);
     return ResponseEntity.ok(CommonResponse.success(false , "User Created Successfully." , userResponseDTO));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Logs in a user and returns a JWT token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<CommonResponse<JwtResponse>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("Login user :)");
        JwtResponse jwtResponse = userService.login(loginRequestDTO);
        log.info("User logged in with email : {}", jwtResponse.getEmail());
        return ResponseEntity.ok(CommonResponse.success(true , "User Logged in Successfully." , jwtResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete user (Admin only)",
            description = "Deletes a user by ID. Requires ADMIN role and valid JWT token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<CommonResponse<UserResponseDTO>> deleteUser(@PathVariable Long id){
        log.info("Delete user with id : {}", id);
        UserResponseDTO userResponseDTO = userService.deleteUser(id);
        log.info("User deleted with User response : {}", userResponseDTO);
        return ResponseEntity.ok(CommonResponse.success(true , "User Deleted successfully." , userResponseDTO));
    }

}
