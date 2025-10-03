package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.Service.EmployeeService;
import com.Ems.EmployeeManagmentSystem.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping("/export-employee")
    @Operation(
            summary = "Export all employees to CSV file",
            description = "Downloads a CSV file containing all employees in the database (active, inactive, and deleted). " +
                    "The file includes employee code, name, email, department, status, and other details. " +
                    "Data is exported in batches for optimal performance with large datasets.",
            tags = {"Admin - Export"}
    )
//    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CSV file generated and downloaded successfully",
                    content = @Content(
                            mediaType = "text/csv",
                            schema = @Schema(type = "string", format = "binary"),
                            examples = @ExampleObject(
                                    name = "CSV Content Example",
                                    value = "Employee Code,First Name,Last Name,Email,Department,Status,Date of Joining,Is Active\n" +
                                            "EMP-2025-000001,John,Doe,john.doe@company.com,Engineering,ACTIVE,2024-01-15,true\n" +
                                            "EMP-2025-000002,Jane,Smith,jane.smith@company.com,HR,ACTIVE,2024-02-20,true"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No employees found in the database",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Unauthorized\", \"message\": \"Invalid token\", \"timestamp\": 1696147200000}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Forbidden\", \"message\": \"Access denied\", \"timestamp\": 1696147200000}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during CSV generation",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Internal Server Error\", \"message\": \"Failed to export employees\", \"timestamp\": 1696147200000}"
                            )
                    )
            )
    })
    public void exportAllEmployeesPaginated(HttpServletResponse response) throws IOException {
        log.info("Admin Controller: exportAllEmployeesPaginated");
        employeeService.exportAllEmployeesPaginated(response);
        log.info("Successfully triggered employee CSV export");
    }

    @GetMapping("/export-users")
    @Operation(
            summary = "Export all users to CSV file",
            description = "Downloads a CSV file containing all users in the database including their roles, status, " +
                    "and authentication details. The file includes user ID, username, email, roles, account status, " +
                    "and timestamps. Data is exported in batches for optimal performance with large datasets.",
            tags = {"Admin - Export"}
    )
//    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CSV file generated and downloaded successfully",
                    content = @Content(
                            mediaType = "text/csv",
                            schema = @Schema(type = "string", format = "binary"),
                            examples = @ExampleObject(
                                    name = "CSV Content Example",
                                    value = "User ID,Username,Email,Role,Is Active,\n" +
                                            "1,admin,admin@company.com,ROLE_ADMIN,true\n" +
                                            "2,john.doe,john.doe@company.com,ROLE_USER,true"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No users found in the database",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Unauthorized\", \"message\": \"Invalid token\", \"timestamp\": 1696147200000}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Forbidden\", \"message\": \"Access denied\", \"timestamp\": 1696147200000}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during CSV generation",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Internal Server Error\", \"message\": \"Failed to export users\", \"timestamp\": 1696147200000}"
                            )
                    )
            )
    })
    public void getAllUsersPaginated(HttpServletResponse response) throws IOException {
        log.info("Admin Controller: getAllUsersPaginated");
        userService.exportUsersPaginated(response);
        log.info("Successfully triggered Users CSV export");
    }

}
