package com.dedalus.interview.repository;

import com.dedalus.interview.entity.Department;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepository<Department> {

    public Optional<Department> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    public List<Department> findAllWithEmployeeCounts() {
        return find("SELECT d FROM Department d LEFT JOIN FETCH d.employees").list();
    }

    public boolean existsByName(String name) {
        return count("name", name) > 0;
    }

    public boolean existsByNameExcludingId(String name, Long id) {
        return count("name = ?1 AND id != ?2", name, id) > 0;
    }
}