package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Service.UserServiceImpl;
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
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
     log.info("Creating a user :)");
     UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
     log.info("User created with User response : {}", userResponseDTO);
     return ResponseEntity.ok(CommonResponse.success(false , "User Created Successfully." , userResponseDTO));
    }

    @GetMapping("/login")
    public ResponseEntity<CommonResponse<JwtResponse>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("Login user :)");
        JwtResponse jwtResponse = userService.login(loginRequestDTO);
        log.info("User logged in with email : {}", jwtResponse.getEmail());
        return ResponseEntity.ok(CommonResponse.success(true , "User Logged in Successfully." , jwtResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponseDTO>> deleteUser(@PathVariable Long id){
        log.info("Delete user with id : {}", id);
        UserResponseDTO userResponseDTO = userService.deleteUser(id);
        log.info("User deleted with User response : {}", userResponseDTO);
        return ResponseEntity.ok(CommonResponse.success(true , "User Deleted successfully." , userResponseDTO));
    }

}
