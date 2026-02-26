package com.dedalus.interview.service;

import com.dedalus.interview.entity.Department;
import com.dedalus.interview.entity.Employee;
import com.dedalus.interview.repository.DepartmentRepository;
import com.dedalus.interview.repository.EmployeeRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    EmployeeService employeeService;

    private Department testDepartment;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Engineering");
        testDepartment.setId(1L);

        testEmployee = new Employee("John Doe", "123 Main St", "555-1234", "john@example.com");
        testEmployee.setId(1L);
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.listAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        verify(employeeRepository).listAll();
    }

    @Test
    void getEmployeeById_WithValidId_ShouldReturnEmployee() {
        when(employeeRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEmployee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals("John Doe", result.getFullName());
        verify(employeeRepository).findByIdOptional(1L);
    }

    @Test
    void getEmployeeById_WithInvalidId_ShouldThrowNotFoundException() {
        when(employeeRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeeById(999L);
        });
    }

    @Test
    void createEmployee_WithValidData_ShouldCreateEmployee() {
        Employee newEmployee = new Employee("Jane Smith", "456 Oak St", "555-5678", "jane@example.com");
        newEmployee.setDepartment(testDepartment);

        when(departmentRepository.findByIdOptional(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.existsByEmailExcludingId("jane@example.com", null)).thenReturn(false);

        Employee result = employeeService.createEmployee(newEmployee);

        assertEquals("Jane Smith", result.getFullName());
        assertEquals(testDepartment, result.getDepartment());
        verify(employeeRepository).persist(newEmployee);
    }

    @Test
    void createEmployee_WithInvalidDepartment_ShouldThrowBadRequestException() {
        Employee newEmployee = new Employee("Jane Smith", "456 Oak St", "555-5678", "jane@example.com");
        Department invalidDept = new Department("Invalid");
        invalidDept.setId(999L);
        newEmployee.setDepartment(invalidDept);

        when(departmentRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            employeeService.createEmployee(newEmployee);
        });
    }

    @Test
    void createEmployee_WithEmptyName_ShouldThrowBadRequestException() {
        Employee newEmployee = new Employee("", "456 Oak St", "555-5678", "jane@example.com");

        assertThrows(BadRequestException.class, () -> {
            employeeService.createEmployee(newEmployee);
        });
    }

    @Test
    void createEmployee_WithDuplicateEmail_ShouldThrowBadRequestException() {
        Employee newEmployee = new Employee("Jane Smith", "456 Oak St", "555-5678", "john@example.com");

        when(employeeRepository.existsByEmailExcludingId("john@example.com", null)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            employeeService.createEmployee(newEmployee);
        });
    }

    @Test
    void searchEmployeesByName_ShouldReturnMatchingEmployees() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByFullNameContainingIgnoreCase("john")).thenReturn(employees);

        List<Employee> result = employeeService.searchEmployeesByName("john");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        verify(employeeRepository).findByFullNameContainingIgnoreCase("john");
    }

    @Test
    void getEmployeesByDepartmentId_ShouldReturnEmployeesInDepartment() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByDepartmentId(1L)).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesByDepartmentId(1L);

        assertEquals(1, result.size());
        verify(employeeRepository).findByDepartmentId(1L);
    }

    @Test
    void getUnassignedEmployees_ShouldReturnUnassignedEmployees() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findUnassigned()).thenReturn(employees);

        List<Employee> result = employeeService.getUnassignedEmployees();

        assertEquals(1, result.size());
        verify(employeeRepository).findUnassigned();
    }

    @Test
    void assignEmployeeToDepartment_ShouldAssignEmployee() {
        when(employeeRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEmployee));
        when(departmentRepository.findByIdOptional(1L)).thenReturn(Optional.of(testDepartment));

        Employee result = employeeService.assignEmployeeToDepartment(1L, 1L);

        assertEquals(testDepartment, result.getDepartment());
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployee() {
        when(employeeRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEmployee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(testEmployee);
    }
}