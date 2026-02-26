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
class DepartmentServiceTest {

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    DepartmentService departmentService;

    private Department testDepartment;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Engineering");
        testDepartment.setId(1L);

        testEmployee = new Employee("John Doe", "123 Main St", "555-1234", "john@example.com");
        testEmployee.setId(1L);
        testEmployee.setDepartment(testDepartment);
    }

    @Test
    void getAllDepartments_ShouldReturnAllDepartments() {
        List<Department> departments = Arrays.asList(testDepartment);
        when(departmentRepository.findAllWithEmployeeCounts()).thenReturn(departments);

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(1, result.size());
        assertEquals("Engineering", result.get(0).getName());
        verify(departmentRepository).findAllWithEmployeeCounts();
    }

    @Test
    void getDepartmentById_WithValidId_ShouldReturnDepartment() {
        when(departmentRepository.findByIdOptional(1L)).thenReturn(Optional.of(testDepartment));

        Department result = departmentService.getDepartmentById(1L);

        assertEquals("Engineering", result.getName());
        verify(departmentRepository).findByIdOptional(1L);
    }

    @Test
    void getDepartmentById_WithInvalidId_ShouldThrowNotFoundException() {
        when(departmentRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            departmentService.getDepartmentById(999L);
        });
    }

    @Test
    void createDepartment_WithValidData_ShouldCreateDepartment() {
        Department newDepartment = new Department("HR");
        when(departmentRepository.existsByName("HR")).thenReturn(false);

        Department result = departmentService.createDepartment(newDepartment);

        assertEquals("HR", result.getName());
        verify(departmentRepository).persist(newDepartment);
    }

    @Test
    void createDepartment_WithDuplicateName_ShouldThrowBadRequestException() {
        Department newDepartment = new Department("Engineering");
        when(departmentRepository.existsByName("Engineering")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            departmentService.createDepartment(newDepartment);
        });
    }

    @Test
    void createDepartment_WithEmptyName_ShouldThrowBadRequestException() {
        Department newDepartment = new Department("");

        assertThrows(BadRequestException.class, () -> {
            departmentService.createDepartment(newDepartment);
        });
    }

    @Test
    void deleteDepartment_ShouldMoveEmployeesToUnassigned() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(departmentRepository.findByIdOptional(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.findByDepartmentId(1L)).thenReturn(employees);

        departmentService.deleteDepartment(1L);

        assertNull(testEmployee.getDepartment());
        verify(departmentRepository).delete(testDepartment);
    }

    @Test
    void getUnassignedEmployeeCount_ShouldReturnCount() {
        when(employeeRepository.count("department IS NULL")).thenReturn(5L);

        long count = departmentService.getUnassignedEmployeeCount();

        assertEquals(5L, count);
        verify(employeeRepository).count("department IS NULL");
    }
}