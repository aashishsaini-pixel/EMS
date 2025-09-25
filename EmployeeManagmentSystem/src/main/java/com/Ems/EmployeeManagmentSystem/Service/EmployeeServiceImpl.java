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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO) {
        log.info("Adding an employee with email: {} and employee code {}", employeeRequestDTO.getEmail() , employeeRequestDTO.getEmployeeCode());

        log.info("EmployeeController:addEmployee");

        List<Employee> employees = employeeRepository.findActiveByEmailOrEmployeeCode(employeeRequestDTO.getEmail(), employeeRequestDTO.getEmployeeCode());

        if(!employees.isEmpty()){
            boolean emailExists = employees.stream().anyMatch(emp -> emp.getEmail().equals(employeeRequestDTO.getEmail()));
            boolean employeeCodeExists = employees.stream().anyMatch(emp -> emp.getEmployeeCode().equals(employeeRequestDTO.getEmployeeCode()));

            if(emailExists && employeeCodeExists){
                log.info("Employee already exists with email {} and employee code {}", employeeRequestDTO.getEmail(), employeeRequestDTO.getEmployeeCode());
                throw new EmployeeAlreadyExistsException("Employee already exists with email " + employeeRequestDTO.getEmail() + " and employee code " + employeeRequestDTO.getEmployeeCode());
            }else if(emailExists){
                log.info("Employee already exists with email {}", employeeRequestDTO.getEmail());
                throw new EmployeeAlreadyExistsException("Employee already exists with email " + employeeRequestDTO.getEmail());
            }else{
                log.info("Employee already exists with employeeCode {}", employeeRequestDTO.getEmployeeCode());
                throw new EmployeeAlreadyExistsException("Employee already exists with Employee Code " + employeeRequestDTO.getEmployeeCode());
            }
        }

        Employee employee = employeeMapper.toEntity(employeeRequestDTO);

         employeeRepository.save(employee);

        log.info("Employee Created Successfully with id {}" , employee.getId());

        return employeeMapper.toResponseDTO(employee);
    }


    @Override
    public EmployeeResponseDTO deleteEmployee(Long id) {
        log.info("EmployeeService:deleteEmployee called with id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with id={}", id);
                    return new EmployeeNotFoundException("Employee not found with id " + id);
                });

        employee.setIsActive(false);
        employeeRepository.save(employee);

        log.info("Employee with id={} marked as inactive (soft deleted)", id);

        return employeeMapper.toResponseDTO(employee);
    }




    @Override
    public Page<EmployeeResponseDTO> getEmployee(String name,EmployeeStatus status,String department,Boolean isActive,int page,int size,String sortBy) {

        log.info("Get employee in the service");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Employee> employees = employeeRepository.findByFilters(name, status, department, isActive, pageable);

        return employees.map(employeeMapper::toResponseDTO);
    }
}
