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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<CommonResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
     log.info("Creating a user :)");
     UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
     log.info("User created with User response : {}", userResponseDTO);
     return ResponseEntity.ok(CommonResponse.success("User Created Successfully." , userResponseDTO));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<JwtResponse>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("Login user :)");
        JwtResponse jwtResponse = userService.login(loginRequestDTO);
        log.info("User logged in with email : {}", jwtResponse.getEmail());
        return ResponseEntity.ok(CommonResponse.success("User Logged in Successfully." , jwtResponse));
    }

}
