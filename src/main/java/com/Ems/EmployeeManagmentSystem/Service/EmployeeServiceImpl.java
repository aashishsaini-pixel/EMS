package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeAlreadyExistsException;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeNotFoundException;
import com.Ems.EmployeeManagmentSystem.Mapper.EmployeeMapper;
import com.Ems.EmployeeManagmentSystem.Repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    @Override
    @Transactional
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO) {
        log.info("Attempting to add employee with email={}", employeeRequestDTO.getEmail());

        if (employeeRepository.existsByEmail(employeeRequestDTO.getEmail())) {
            log.info("Employee with email={} already exists", employeeRequestDTO.getEmail());
            throw new EmployeeAlreadyExistsException("Employee already exists with email " + employeeRequestDTO.getEmail());
        }

        Employee employee = employeeMapper.toEntity(employeeRequestDTO);

        employeeRepository.save(employee);

        employee.setEmployeeCode("Emp-" + employee.getId());

        employeeRepository.save(employee);

        log.info("Employee created successfully with ID={}", employee.getId());
        return employeeMapper.toResponseDTO(employee);
    }


    @Override
    @Transactional
    public EmployeeResponseDTO deleteEmployee(Long id) {
        log.info("Request to delete employee with ID={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID={}", id);
                    return new EmployeeNotFoundException("Employee not found with ID " + id);
                });

        employee.setIsActive(false);
        employee.setIsDeleted(true);

        employeeRepository.save(employee);

        log.info("Employee with ID={} marked as inactive and deleted", id);
        return employeeMapper.toResponseDTO(employee);
    }


    @Override
    public Page<EmployeeResponseDTO> getEmployee(String name, EmployeeStatus status, String department, Boolean isActive, int page, int size, String sortBy) {
        log.info("Fetching employees with filters: name={}, status={}, department={}, isActive={}", name, status, department, isActive);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Employee> employees = employeeRepository.findByFilters(name, status, department, isActive, pageable);

        log.info("Employees found successfully with total employee in page {} size {} is {}", page, size, employees.getTotalElements());

        return employees.map(employeeMapper::toResponseDTO);
    }


    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO) {
        log.info("Attempting to update employee with ID={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID={}", id);
                    return new EmployeeNotFoundException("Employee not found with ID " + id);
                });

        if (!Boolean.TRUE.equals(employee.getIsActive()) || Boolean.TRUE.equals(employee.getIsDeleted())) {
            log.warn("Cannot update inactive or deleted employee with ID={}", id);
            throw new IllegalStateException("Cannot update inactive or deleted employee with ID " + id);
        }

        boolean emailExists = employeeRepository.existsByEmailAndIdNot(employeeRequestDTO.getEmail(), id);
        if (emailExists) {
            log.info("Email {} already used by another employee", employeeRequestDTO.getEmail());
            throw new EmployeeAlreadyExistsException("Email " + employeeRequestDTO.getEmail() + " is already in use.");
        }

        employeeMapper.updateEmployee(employeeRequestDTO, employee);

        employeeRepository.save(employee);

        log.info("Employee updated successfully. ID={}, employeeCode={}", employee.getId(), employee.getEmployeeCode());

        return employeeMapper.toResponseDTO(employee);
    }
}
