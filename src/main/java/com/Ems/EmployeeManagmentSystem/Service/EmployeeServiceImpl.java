package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.EmployeeResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeAlreadyExistsException;
import com.Ems.EmployeeManagmentSystem.Exceptions.EmployeeNotFoundException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserNotFoundException;
import com.Ems.EmployeeManagmentSystem.Mapper.EmployeeMapper;
import com.Ems.EmployeeManagmentSystem.Repository.EmployeeRepository;
import com.Ems.EmployeeManagmentSystem.Repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final UsersRepository usersRepository;


    @Value("${employee.import.batch-size}")
    private int BATCH_SIZE;

    @Transactional
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO) {
        final String requestEmail = employeeRequestDTO.getEmail();
        log.info("Starting employee creation process for email: {}", requestEmail);

        try {
            Users authenticatedUser = validateAndGetAuthenticatedUser();
            log.debug("Authenticated user ID: {}, Username: {}",
                    authenticatedUser.getId(), authenticatedUser.getEmail());

            validateUniqueEmail(requestEmail);

            Employee employee = createEmployee(employeeRequestDTO, authenticatedUser);
            log.info("Employee created successfully with ID: {}, Email: {}, Code: {}",
                    employee.getId(), employee.getEmail(), employee.getEmployeeCode());

            EmployeeResponseDTO responseDTO = employeeMapper.toResponseDTO(employee);
            log.debug("Employee response DTO created for ID: {}", employee.getId());

            return responseDTO;

        } catch (UserNotFoundException e) {
            log.error("Failed to add employee - User not found: {}", e.getMessage());
            throw e;
        } catch (EmployeeAlreadyExistsException e) {
            log.warn("Failed to add employee - Duplicate email detected: {}", requestEmail);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding employee with email: {}", requestEmail, e);
            throw new RuntimeException("Unexpected error while adding employee with email: " + requestEmail, e);
        }
    }

    private Users validateAndGetAuthenticatedUser() {
        log.debug("Validating authenticated user from security context");

        Users authUser = getUser().orElseThrow(() -> {
            log.error("Authentication failed - No user found in security context");
            return new UserNotFoundException("User not authenticated");
        });

        log.debug("Found authenticated user with ID: {}", authUser.getId());

        Users managedUser = usersRepository.findById(authUser.getId())
                .orElseThrow(() -> {
                    log.error("User with ID {} exists in security context but not in database", authUser.getId());
                    return new UserNotFoundException("User not found with ID: " + authUser.getId());
                });

        log.debug("Successfully retrieved managed user entity for ID: {}", managedUser.getId());
        return managedUser;
    }

    private void validateUniqueEmail(String email) {
        log.debug("Checking if employee with email {} already exists", email);

        if (employeeRepository.existsByEmail(email)) {
            log.warn("Employee registration failed - Email already exists: {}", email);
            throw new EmployeeAlreadyExistsException(
                    String.format("Employee already exists with email: %s", email)
            );
        }

        log.debug("Email {} is unique and available", email);
    }

    private Employee createEmployee(EmployeeRequestDTO employeeRequestDTO, Users user) {
        log.debug("Mapping employee request DTO to entity");

        Employee employee = employeeMapper.toEntity(employeeRequestDTO);
        employee.setUser(user);

        log.debug("Persisting employee entity for email: {}", employeeRequestDTO.getEmail());

        employee = employeeRepository.save(employee);

        String employeeCode = generateEmployeeCode(employee.getId());
        employee.setEmployeeCode(employeeCode);

        log.debug("Generated employee code: {} for ID: {}", employeeCode, employee.getId());

        employee = employeeRepository.save(employee);

        user.setEmployee(employee);

        log.debug("Updating user-employee bidirectional relationship for user ID: {}", user.getId());

        employeeRepository.flush();
        usersRepository.flush();

        log.info("Employee entity persisted successfully with ID: {}, Code: {}",
                employee.getId(), employee.getEmployeeCode());

        return employee;
    }

    private String generateEmployeeCode(Long employeeId) {
        String year = String.valueOf(java.time.Year.now().getValue());
        String paddedId = String.format("%06d", employeeId);
        return String.format("EMP-%s-%s", year, paddedId);
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

    @Override
    public EmployeeResponseDTO getLoggedInUser() {
        log.info("Request to get employee for logged in user");

        Users user = getUser().orElseThrow(() -> {
            log.info("Logged-in user not found");
            return new UserNotFoundException("User not found");
        });

        log.info("Logged-in user found: {}", user.getEmail());

        try {
            Employee employee = user.getEmployee();
            return employeeMapper.toResponseDTO(employee);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving employee for user: {}", user.getEmail(), e);
            throw new RuntimeException("Unexpected error while retrieving logged-in employee", e);
        }
    }

    @Transactional
    public void exportAllEmployeesPaginated(HttpServletResponse response) throws IOException {
        log.info("Starting CSV export for all employees");
        long startTime = System.currentTimeMillis();

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("employees_export_%s.csv", timestamp);

            response.setContentType("text/csv; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);

            writer.write('\ufeff');

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("Employee Code", "First Name", "Last Name", "Email",
                            "Department", "Status", "Date of Joining", "Is Active" , "User Email")
                    .setDelimiter(',')
                    .setRecordSeparator("\r\n")
                    .setQuoteMode(org.apache.commons.csv.QuoteMode.MINIMAL)
                    .build();

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

                int pageNumber = 0;
                int batchSize = BATCH_SIZE;
                int totalExported = 0;
                Page<Employee> page;

                do {
                    Pageable pageable = PageRequest.of(pageNumber, batchSize,
                            Sort.by(Sort.Direction.ASC, "id"));

                    page = employeeRepository.findAll(pageable);

                    log.debug("Processing batch {}: {} employees", pageNumber + 1, page.getNumberOfElements());

                    for (Employee employee : page.getContent()) {
                        log.info("Processing employee {}", employee);
                        csvPrinter.printRecord(
                                employee.getEmployeeCode() != null ? employee.getEmployeeCode() : "",
                                employee.getFirstName() != null ? employee.getFirstName() : "",
                                employee.getLastName() != null ? employee.getLastName() : "",
                                employee.getEmail() != null ? employee.getEmail() : "",
                                employee.getDepartment() != null ? employee.getDepartment() : "",
                                employee.getStatus() != null ? employee.getStatus().name() : "",
                                employee.getDateOfJoining() != null ?
                                        employee.getDateOfJoining().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "",
                                employee.getIsActive() != null ? employee.getIsActive().toString() : "false",
                                employee.getUser() != null? employee.getUser().getEmail() : ""
                        );
                        totalExported++;
                    }

                    pageNumber++;

                } while (page.hasNext());

                csvPrinter.flush();

                long duration = System.currentTimeMillis() - startTime;
                log.info("CSV export completed successfully. Total employees: {}, Duration: {}ms",
                        totalExported, duration);
            }

        } catch (IOException e) {
            log.error("Error during CSV export", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during CSV export", e);
            throw new RuntimeException("Failed to export employees to CSV", e);
        }
    }




    public Optional<Users> getUser() {
        log.info("Getting the user from the security context holder :) ");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            Users currentUser = ((CustomUserDetails) principal).getUser();
            return Optional.of(currentUser);
        } else {
            log.warn("Principal is not an instance of CustomUserDetails");
            return Optional.empty();
        }
    }

}
