package com.dedalus.interview.repository;

import com.dedalus.interview.entity.Employee;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {

    public List<Employee> findByDepartmentId(Long departmentId) {
        if (departmentId == null) {
            return find("department IS NULL").list();
        }
        return find("department.id", departmentId).list();
    }

    public List<Employee> findByFullNameContainingIgnoreCase(String name) {
        return find("LOWER(fullName) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    public List<Employee> findUnassigned() {
        return find("department IS NULL").list();
    }

    public List<Employee> findByDepartmentIdAndFullNameContainingIgnoreCase(Long departmentId, String name) {
        if (departmentId == null) {
            return find("department IS NULL AND LOWER(fullName) LIKE LOWER(?1)", "%" + name + "%").list();
        }
        return find("department.id = ?1 AND LOWER(fullName) LIKE LOWER(?2)", departmentId, "%" + name + "%").list();
    }

    public boolean existsByEmailExcludingId(String email, Long excludeId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (excludeId == null) {
            return count("email = ?1", email.trim()) > 0;
        }
        return count("email = ?1 AND id != ?2", email.trim(), excludeId) > 0;
    }
}