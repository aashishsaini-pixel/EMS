package com.Ems.EmployeeManagmentSystem.Controller;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Service.EmployeeServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("employee/api")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;


    @PostMapping
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> addEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {

        log.info("EmployeeController:addEmployee");

        EmployeeResponseDTO employeeResponseDTO = employeeService.addEmployee(employeeRequestDTO);

        log.info("Employee Added with Employee Id {}", employeeResponseDTO.getId());

        return ResponseEntity.ok(CommonResponse.success("Employee added successfully", employeeResponseDTO));

    }

    @GetMapping
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
        return ResponseEntity.ok(CommonResponse.success("Employees fetched successfully", dtoPage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<EmployeeResponseDTO>> deleteEmployee(@PathVariable Long id) {
        log.info("EmployeeController:deleteEmployee called with id={}", id);

        EmployeeResponseDTO deletedEmployee = employeeService.deleteEmployee(id);

        log.info("EmployeeController:deleteEmployee successfully deleted employee with id={}", id);

        return ResponseEntity.ok(CommonResponse.success("Employee deleted successfully", deletedEmployee));
    }


}
