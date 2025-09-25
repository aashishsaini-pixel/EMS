package com.Ems.EmployeeManagmentSystem.Entity;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employee extends AbstractAudiatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    @Column(name = "date_of_joining", nullable = false)
    private LocalDate dateOfJoining;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeCode='" + employeeCode + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", status=" + status +
                ", dateOfJoining=" + dateOfJoining +
                ", isActive=" + isActive +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId()) && Objects.equals(getEmployeeCode(), employee.getEmployeeCode()) && Objects.equals(getFirstName(), employee.getFirstName()) && Objects.equals(getLastName(), employee.getLastName()) && Objects.equals(getEmail(), employee.getEmail()) && Objects.equals(getDepartment(), employee.getDepartment()) && getStatus() == employee.getStatus() && Objects.equals(getDateOfJoining(), employee.getDateOfJoining()) && Objects.equals(getIsActive(), employee.getIsActive()) && Objects.equals(getCreatedAt(), employee.getCreatedAt()) && Objects.equals(getUpdatedAt(), employee.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployeeCode(), getFirstName(), getLastName(), getEmail(), getDepartment(), getStatus(), getDateOfJoining(), getIsActive(), getCreatedAt(), getUpdatedAt());
    }
}
