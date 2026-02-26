package com.dedalus.interview.service;

import com.dedalus.interview.entity.Employee;
import com.dedalus.interview.entity.Department;
import com.dedalus.interview.repository.EmployeeRepository;
import com.dedalus.interview.repository.DepartmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class EmployeeService {

    private static final Logger logger = Logger.getLogger(EmployeeService.class.getName());

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    DepartmentRepository departmentRepository;

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.listAll();
    }

    public Employee getEmployeeById(Long id) {
        logger.info("Fetching employee with id: " + id);
        return employeeRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
    }

    public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
        logger.info("Fetching employees for department id: " + departmentId);
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public List<Employee> getUnassignedEmployees() {
        logger.info("Fetching unassigned employees");
        return employeeRepository.findUnassigned();
    }

    public List<Employee> searchEmployeesByName(String name) {
        logger.info("Searching employees by name: " + name);
        if (name == null || name.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.findByFullNameContainingIgnoreCase(name.trim());
    }

    public List<Employee> searchEmployeesInDepartment(Long departmentId, String name) {
        logger.info("Searching employees in department " + departmentId + " by name: " + name);
        if (name == null || name.trim().isEmpty()) {
            return getEmployeesByDepartmentId(departmentId);
        }
        return employeeRepository.findByDepartmentIdAndFullNameContainingIgnoreCase(departmentId, name.trim());
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        logger.info("Creating new employee: " + employee.getFullName());

        validateEmployee(employee, null);

        if (employee.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdOptional(employee.getDepartmentId())
                    .orElseThrow(() -> new BadRequestException("Department not found with id: " + employee.getDepartmentId()));
            employee.setDepartment(department);
        }

        employeeRepository.persist(employee);
        logger.info("Created employee with id: " + employee.getId());
        return employee;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employeeUpdate) {
        logger.info("Updating employee with id: " + id);

        Employee existingEmployee = getEmployeeById(id);

        if (employeeUpdate.getFullName() != null) {
            existingEmployee.setFullName(employeeUpdate.getFullName().trim());
        }
        if (employeeUpdate.getAddress() != null) {
            existingEmployee.setAddress(employeeUpdate.getAddress().trim());
        }
        if (employeeUpdate.getPhone() != null) {
            existingEmployee.setPhone(employeeUpdate.getPhone().trim());
        }
        if (employeeUpdate.getEmail() != null) {
            existingEmployee.setEmail(employeeUpdate.getEmail().trim());
        }

        validateEmployee(existingEmployee, id);

        if (employeeUpdate.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdOptional(employeeUpdate.getDepartmentId())
                    .orElseThrow(() -> new BadRequestException("Department not found with id: " + employeeUpdate.getDepartmentId()));
            existingEmployee.setDepartment(department);
        } else if (employeeUpdate.getDepartment() == null) {
            existingEmployee.setDepartment(null);
        }

        logger.info("Updated employee: " + existingEmployee.getFullName());
        return existingEmployee;
    }

    @Transactional
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with id: " + id);

        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);

        logger.info("Deleted employee: " + employee.getFullName());
    }

    @Transactional
    public Employee assignEmployeeToDepartment(Long employeeId, Long departmentId) {
        logger.info("Assigning employee " + employeeId + " to department " + departmentId);

        Employee employee = getEmployeeById(employeeId);

        if (departmentId != null) {
            Department department = departmentRepository.findByIdOptional(departmentId)
                    .orElseThrow(() -> new BadRequestException("Department not found with id: " + departmentId));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        logger.info("Assigned employee " + employee.getFullName() + " to " +
                   (departmentId != null ? "department " + departmentId : "unassigned"));
        return employee;
    }

    private void validateEmployee(Employee employee, Long excludeId) {
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Employee full name is required");
        }

        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty()) {
            if (employeeRepository.existsByEmailExcludingId(employee.getEmail(), excludeId)) {
                throw new BadRequestException("Employee with email '" + employee.getEmail() + "' already exists");
            }
        }
    }
}