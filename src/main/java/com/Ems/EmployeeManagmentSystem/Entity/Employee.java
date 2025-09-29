package com.Ems.EmployeeManagmentSystem.Entity;

import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee extends AbstractAudiatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", unique = true, length = 20)
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

    @Column(name = "is_deleted" , nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Users user;

}
