package com.dedalus.interview.service;

import com.dedalus.interview.entity.Department;
import com.dedalus.interview.entity.Employee;
import com.dedalus.interview.repository.DepartmentRepository;
import com.dedalus.interview.repository.EmployeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class DepartmentService {

    private static final Logger logger = Logger.getLogger(DepartmentService.class.getName());

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    EmployeeRepository employeeRepository;

    public List<Department> getAllDepartments() {
        logger.info("Fetching all departments with employee counts");
        return departmentRepository.findAllWithEmployeeCounts();
    }

    public Department getDepartmentById(Long id) {
        logger.info("Fetching department with id: " + id);
        return departmentRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
    }

    public Department getDepartmentByName(String name) {
        logger.info("Fetching department with name: " + name);
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Department not found with name: " + name));
    }

    @Transactional
    public Department createDepartment(Department department) {
        logger.info("Creating new department: " + department.getName());

        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BadRequestException("Department name is required");
        }

        String trimmedName = department.getName().trim();
        if (departmentRepository.existsByName(trimmedName)) {
            throw new BadRequestException("Department with name '" + trimmedName + "' already exists");
        }

        department.setName(trimmedName);
        departmentRepository.persist(department);
        logger.info("Created department with id: " + department.getId());
        return department;
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentUpdate) {
        logger.info("Updating department with id: " + id);

        Department existingDepartment = getDepartmentById(id);

        if (departmentUpdate.getName() != null) {
            String trimmedName = departmentUpdate.getName().trim();
            if (trimmedName.isEmpty()) {
                throw new BadRequestException("Department name cannot be empty");
            }

            if (departmentRepository.existsByNameExcludingId(trimmedName, id)) {
                throw new BadRequestException("Department with name '" + trimmedName + "' already exists");
            }

            existingDepartment.setName(trimmedName);
        }

        // Force load employees to calculate count
        existingDepartment.getEmployees().size();

        logger.info("Updated department: " + existingDepartment.getName());
        return existingDepartment;
    }

    @Transactional
    public void deleteDepartment(Long id) {
        logger.info("Deleting department with id: " + id);

        Department department = getDepartmentById(id);

        List<Employee> employees = employeeRepository.findByDepartmentId(id);
        for (Employee employee : employees) {
            employee.setDepartment(null);
            logger.info("Moved employee " + employee.getFullName() + " to unassigned");
        }

        departmentRepository.delete(department);
        logger.info("Deleted department: " + department.getName());
    }

    public long getUnassignedEmployeeCount() {
        return employeeRepository.count("department IS NULL");
    }
}