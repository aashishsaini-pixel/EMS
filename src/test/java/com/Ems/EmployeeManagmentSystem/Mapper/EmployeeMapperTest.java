package com.Ems.EmployeeManagmentSystem.Mapper;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import com.Ems.EmployeeManagmentSystem.dto.Request.EmployeeRequestDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import com.Ems.EmployeeManagmentSystem.dto.Response.EmployeeResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void testToEntity_Success() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setDepartment("Engineering");

        Employee employee = employeeMapper.toEntity(dto);

        assertNotNull(employee);
        assertEquals(dto.getFirstName(), employee.getFirstName());
        assertEquals(dto.getLastName(), employee.getLastName());
        assertEquals(dto.getEmail(), employee.getEmail());
        assertEquals(dto.getDepartment(), employee.getDepartment());
    }

    @Test
    void testToResponseDTO_Success() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Alice");
        employee.setLastName("Smith");
        employee.setEmail("alice.smith@example.com");
        employee.setDepartment("HR");
        employee.setIsActive(true);
        employee.setIsDeleted(false);

        var dto = employeeMapper.toResponseDTO(employee);

        assertNotNull(dto);
        assertEquals(employee.getId(), dto.getId());
        assertEquals(employee.getFirstName(), dto.getFirstName());
        assertEquals(employee.getLastName(), dto.getLastName());
        assertEquals(employee.getEmail(), dto.getEmail());
        assertEquals(employee.getDepartment(), dto.getDepartment());
        assertEquals(employee.getIsActive(), dto.getIsActive());
    }

    @Test
    void testUpdateEmployee_IgnoresNullFields() {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDepartment("Engineering");

        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setFirstName("Jane");
        requestDTO.setDepartment(null);

        employeeMapper.updateEmployee(requestDTO, employee);

        assertEquals("Jane", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    void testUpdateEmployee_AllFieldsNull_NoChange() {
        Employee employee = new Employee();
        employee.setFirstName("Alice");
        employee.setLastName("Smith");
        employee.setEmail("alice.smith@example.com");
        employee.setDepartment("Finance");

        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();

        employeeMapper.updateEmployee(requestDTO, employee);

        assertEquals("Alice", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("alice.smith@example.com", employee.getEmail());
        assertEquals("Finance", employee.getDepartment());
    }

    @Test
    void testToEntity_NullStatus() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setFirstName("Alice");
        request.setLastName("Wonderland");
        request.setEmail("alice@example.com");
        request.setDepartment("Finance");
        request.setStatus(null); // status not set
        request.setDateOfJoining(LocalDate.now());

        Employee entity = employeeMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Alice", entity.getFirstName());
        assertNull(entity.getStatus()); // should not be set
    }

    @Test
    void testToResponseDTO_NullInput() {
        EmployeeResponseDTO dto = employeeMapper.toResponseDTO(null);
        assertNull(dto);
    }

    @Test
    void testUpdateEmployee_StatusChange() {
        Employee employee = new Employee();
        employee.setStatus(EmployeeStatus.INACTIVE);

        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setStatus("ACTIVE");

        employeeMapper.updateEmployee(dto, employee);

        assertEquals(EmployeeStatus.ACTIVE, employee.getStatus());
    }

    @Test
    void testUpdateEmployee_PartialFields() {
        Employee employee = new Employee();
        employee.setFirstName("Old");
        employee.setLastName("Name");
        employee.setEmail("old@example.com");
        employee.setDepartment("HR");

        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setEmail(null);
        dto.setFirstName("New");

        employeeMapper.updateEmployee(dto, employee);

        assertEquals("New", employee.getFirstName());
        assertEquals("Name", employee.getLastName());
        assertEquals("old@example.com", employee.getEmail());
        assertEquals("HR", employee.getDepartment());
    }

    @Test
    void testUpdateEmployee_DateUpdate() {
        Employee employee = new Employee();
        LocalDate newDate = LocalDate.now();
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setDateOfJoining(newDate);

        employeeMapper.updateEmployee(dto, employee);

        assertEquals(newDate, employee.getDateOfJoining());
    }

}
