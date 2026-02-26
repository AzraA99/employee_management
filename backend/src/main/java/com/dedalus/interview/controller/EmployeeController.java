package com.dedalus.interview.controller;

import com.dedalus.interview.dto.EmployeeSummaryDto;
import com.dedalus.interview.entity.Employee;
import com.dedalus.interview.service.EmployeeService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Path("/api/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Employees", description = "Employee management operations")
public class EmployeeController {

    private static final Logger logger = Logger.getLogger(EmployeeController.class.getName());

    @Inject
    EmployeeService employeeService;

    @GET
    @Operation(summary = "Get all employees or search by name")
    @APIResponse(responseCode = "200", description = "List of employees")
    public List<EmployeeSummaryDto> getAllEmployees(@QueryParam("search") String search) {
        logger.info("GET /api/employees" + (search != null ? "?search=" + search : ""));

        List<Employee> employees;
        if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployeesByName(search);
        } else {
            employees = employeeService.getAllEmployees();
        }

        return employees.stream()
                .map(EmployeeSummaryDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get employee by ID")
    @APIResponse(responseCode = "200", description = "Employee found")
    @APIResponse(responseCode = "404", description = "Employee not found")
    public Employee getEmployeeById(@Parameter(description = "Employee ID") @PathParam("id") Long id) {
        logger.info("GET /api/employees/" + id);
        return employeeService.getEmployeeById(id);
    }

    @GET
    @Path("/department/{departmentId}")
    @Operation(summary = "Get employees by department")
    @APIResponse(responseCode = "200", description = "List of employees in department")
    public List<EmployeeSummaryDto> getEmployeesByDepartment(
            @Parameter(description = "Department ID") @PathParam("departmentId") Long departmentId,
            @QueryParam("search") String search) {
        logger.info("GET /api/employees/department/" + departmentId + (search != null ? "?search=" + search : ""));

        List<Employee> employees;
        if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployeesInDepartment(departmentId, search);
        } else {
            employees = employeeService.getEmployeesByDepartmentId(departmentId);
        }

        return employees.stream()
                .map(EmployeeSummaryDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/unassigned")
    @Operation(summary = "Get unassigned employees")
    @APIResponse(responseCode = "200", description = "List of unassigned employees")
    public List<EmployeeSummaryDto> getUnassignedEmployees(@QueryParam("search") String search) {
        logger.info("GET /api/employees/unassigned" + (search != null ? "?search=" + search : ""));

        List<Employee> employees;
        if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployeesInDepartment(null, search);
        } else {
            employees = employeeService.getUnassignedEmployees();
        }

        return employees.stream()
                .map(EmployeeSummaryDto::new)
                .collect(Collectors.toList());
    }

    @POST
    @Operation(summary = "Create a new employee")
    @APIResponse(responseCode = "201", description = "Employee created successfully")
    @APIResponse(responseCode = "400", description = "Invalid employee data")
    public Response createEmployee(@Valid Employee employee) {
        logger.info("POST /api/employees - Creating employee: " + employee.getFullName());

        try {
            Employee created = employeeService.createEmployee(employee);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error creating employee: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DepartmentController.ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing employee")
    @APIResponse(responseCode = "200", description = "Employee updated successfully")
    @APIResponse(responseCode = "400", description = "Invalid employee data")
    @APIResponse(responseCode = "404", description = "Employee not found")
    public Response updateEmployee(@Parameter(description = "Employee ID") @PathParam("id") Long id,
                                 @Valid Employee employee) {
        logger.info("PUT /api/employees/" + id);

        try {
            Employee updated = employeeService.updateEmployee(id, employee);
            return Response.ok(updated).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error updating employee: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DepartmentController.ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an employee")
    @APIResponse(responseCode = "204", description = "Employee deleted successfully")
    @APIResponse(responseCode = "404", description = "Employee not found")
    public Response deleteEmployee(@Parameter(description = "Employee ID") @PathParam("id") Long id) {
        logger.info("DELETE /api/employees/" + id);

        try {
            employeeService.deleteEmployee(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error deleting employee: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DepartmentController.ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/department/{departmentId}")
    @Operation(summary = "Assign employee to department")
    @APIResponse(responseCode = "200", description = "Employee assigned successfully")
    @APIResponse(responseCode = "404", description = "Employee or department not found")
    public Response assignToDepartment(
            @Parameter(description = "Employee ID") @PathParam("id") Long employeeId,
            @Parameter(description = "Department ID") @PathParam("departmentId") Long departmentId) {
        logger.info("PUT /api/employees/" + employeeId + "/department/" + departmentId);

        try {
            Employee updated = employeeService.assignEmployeeToDepartment(employeeId, departmentId);
            return Response.ok(updated).build();
        } catch (NotFoundException | BadRequestException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error assigning employee to department: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DepartmentController.ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}/department")
    @Operation(summary = "Remove employee from department (make unassigned)")
    @APIResponse(responseCode = "200", description = "Employee unassigned successfully")
    @APIResponse(responseCode = "404", description = "Employee not found")
    public Response removeFromDepartment(@Parameter(description = "Employee ID") @PathParam("id") Long employeeId) {
        logger.info("DELETE /api/employees/" + employeeId + "/department");

        try {
            Employee updated = employeeService.assignEmployeeToDepartment(employeeId, null);
            return Response.ok(updated).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new DepartmentController.ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error removing employee from department: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new DepartmentController.ErrorResponse("Internal server error"))
                    .build();
        }
    }
}