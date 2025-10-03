package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Service.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Controller", description = "Endpoints related to employee management")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;


    @PostMapping
    @Operation(summary = "Add a new employee", description = "Creates a new employee. Admin or user access may be required depending on configuration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee added successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> addEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {

        log.info("EmployeeController:addEmployee");

        EmployeeResponseDTO employeeResponseDTO = employeeService.addEmployee(employeeRequestDTO);

        log.info("Employee Added with Employee Id {}", employeeResponseDTO.getId());

        return ResponseEntity.ok(CommonResponse.success(true , "Employee added successfully", employeeResponseDTO));

    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all employees (admin only)", description = "Returns a paginated list of all employees. Admin access required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees fetched successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<CommonResponse<Page<EmployeeResponseDTO>>> getEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        log.info("EmployeeController:getEmployees");
        Page<EmployeeResponseDTO> dtoPage = employeeService.getEmployee(name, status, department, isActive, page, size, sortBy);
        log.info("Getting total number of Employees {}", dtoPage.getTotalElements());
        return ResponseEntity.ok(CommonResponse.success(true ,"Employees fetched successfully", dtoPage));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get logged-in employee", description = "Returns the profile of the currently logged-in employee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee fetched successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> getEmployees() {
        log.info("EmployeeController:getEmployees - Getting the logged in User");
        EmployeeResponseDTO loggedInUser = employeeService.getLoggedInUser();
        log.info("Returning logged-in employee: {}", loggedInUser);
        return ResponseEntity.ok(CommonResponse.success(true ,"Employee fetched successfully", loggedInUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete employee (admin only)", description = "Deletes an employee by their ID. Admin access required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> deleteEmployee(@PathVariable Long id) {
        log.info("EmployeeController:deleteEmployee called with id={}", id);

        EmployeeResponseDTO deletedEmployee = employeeService.deleteEmployee(id);

        log.info("EmployeeController:deleteEmployee successfully deleted employee with id={}", id);

        return ResponseEntity.ok(CommonResponse.success(true , "Employee deleted successfully", deletedEmployee));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update employee", description = "Updates an existing employee by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> updateEmployee(@PathVariable Long id , @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        log.info("Received request to update Employee with ID: {}", id);
        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(id , employeeRequestDTO);
        return  ResponseEntity.ok(CommonResponse.success(true , "Employee updated successfully", updatedEmployee));
    }

}
