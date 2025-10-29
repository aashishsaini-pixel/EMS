//package com.Ems.EmployeeManagmentSystem.Repository;
//
//import com.Ems.EmployeeManagmentSystem.Entity.Employee;
//import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
//class EmployeeRepositoryTest {
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    // Helper method to create a sample employee
//    private Employee createEmployee(String firstName, String lastName, String email, String department, EmployeeStatus status, boolean isActive) {
//        Employee employee = new Employee();
//        employee.setFirstName(firstName);
//        employee.setLastName(lastName);
//        employee.setEmail(email);
//        employee.setDepartment(department);
//        employee.setStatus(status);
//        employee.setIsActive(isActive);
//        return employee;
//    }
//
//    // Test data setup
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void findByFilters_ShouldReturnFilteredEmployees() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // When
//        Page<Employee> result = employeeRepository.findByFilters("John", EmployeeStatus.ACTIVE, "IT", true, pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void findByFilters_ShouldReturnAllEmployees_WhenAllFiltersAreNull() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // When
//        Page<Employee> result = employeeRepository.findByFilters(null, null, null, null, pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(3);
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void findByFilters_ShouldReturnEmployeesByName() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // When
//        Page<Employee> result = employeeRepository.findByFilters("Doe", null, null, null, pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void findByFilters_ShouldReturnEmployeesByStatus() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // When
//        Page<Employee> result = employeeRepository.findByFilters(null, EmployeeStatus.INACTIVE, null, null, pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void findByFilters_ShouldReturnEmployeesByDepartment() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // When
//        Page<Employee> result = employeeRepository.findByFilters(null, null, "HR", null, pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getDepartment()).isEqualTo("HR");
//    }
//
////    @Test
////    @Sql(scripts = "/test-data.sql")
////    void findByFilters_ShouldReturnEmployeesByIsActive() {
////        // Given
////        Pageable pageable = PageRequest.of(0, 10);
////
////        // When
////        Page<Employee> result = employeeRepository.findByFilters(null, null, null, false, pageable);
////
////        // Then
////        assertThat(result).isNotNull();
////        assertThat(result.getContent()).hasSize(1);
////        assertThat(result.getContent().get(0).isActive()).isFalse();
////    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void existsByEmail_ShouldReturnTrue_IfEmailExists() {
//        // When
//        boolean exists = employeeRepository.existsByEmail("john.doe@example.com");
//
//        // Then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void existsByEmail_ShouldReturnFalse_IfEmailDoesNotExist() {
//        // When
//        boolean exists = employeeRepository.existsByEmail("nonexistent@example.com");
//
//        // Then
//        assertThat(exists).isFalse();
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void existsByEmailAndIdNot_ShouldReturnTrue_IfEmailExistsForAnotherId() {
//        // When
//        boolean exists = employeeRepository.existsByEmailAndIdNot("john.doe@example.com", 2L);
//
//        // Then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @Sql(scripts = "/test-data.sql")
//    void existsByEmailAndIdNot_ShouldReturnFalse_IfEmailDoesNotExistForAnotherId() {
//        // When
//        boolean exists = employeeRepository.existsByEmailAndIdNot("jane.smith@example.com", 1L);
//
//        // Then
//        assertThat(exists).isFalse();
//    }
//}
