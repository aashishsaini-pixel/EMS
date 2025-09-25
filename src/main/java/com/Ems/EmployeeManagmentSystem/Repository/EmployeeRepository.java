package com.Ems.EmployeeManagmentSystem.Repository;

import com.Ems.EmployeeManagmentSystem.Entity.Employee;
import com.Ems.EmployeeManagmentSystem.Enum.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND (e.email = :email OR e.employeeCode = :employeeCode)")
    List<Employee> findActiveByEmailOrEmployeeCode(@Param("email") String email , @Param("employeeCode") String employeeCode);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:name IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
                          OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:status IS NULL OR e.status = :status)
          AND (:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%')))
          AND (:isActive IS NULL OR e.isActive = :isActive)
    """)
    Page<Employee> findByFilters(
            @Param("name") String name,
            @Param("status") EmployeeStatus status,
            @Param("department") String department,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
