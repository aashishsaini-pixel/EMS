package com.Ems.EmployeeManagmentSystem.Entity;

import com.Ems.EmployeeManagmentSystem.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users") // Still using 'users' as the table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends AbstractAudiatable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name =  "is_deleted", nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @OneToOne(mappedBy = "user")
    private Employee employee;

}
