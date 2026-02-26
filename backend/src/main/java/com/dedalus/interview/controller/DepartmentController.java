package com.dedalus.interview.controller;

import com.dedalus.interview.dto.DepartmentSummaryDto;
import com.dedalus.interview.entity.Department;
import com.dedalus.interview.service.DepartmentService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/api/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Departments", description = "Department management operations")
public class DepartmentController {

    private static final Logger logger = Logger.getLogger(DepartmentController.class.getName());

    @Inject
    DepartmentService departmentService;

    @GET
    @Operation(summary = "Get all departments with employee counts")
    @APIResponse(responseCode = "200", description = "List of departments with employee counts")
    public List<DepartmentSummaryDto> getAllDepartments() {
        logger.info("GET /api/departments - Getting all departments");

        List<Department> departments = departmentService.getAllDepartments();
        List<DepartmentSummaryDto> summaries = new ArrayList<>();

        for (Department dept : departments) {
            summaries.add(new DepartmentSummaryDto(dept));
        }

        long unassignedCount = departmentService.getUnassignedEmployeeCount();
        if (unassignedCount > 0) {
            summaries.add(new DepartmentSummaryDto(null, "Unassigned", (int) unassignedCount));
        }

        return summaries;
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get department by ID")
    @APIResponse(responseCode = "200", description = "Department found")
    @APIResponse(responseCode = "404", description = "Department not found")
    public Department getDepartmentById(@Parameter(description = "Department ID") @PathParam("id") Long id) {
        logger.info("GET /api/departments/" + id);
        return departmentService.getDepartmentById(id);
    }

    @POST
    @Operation(summary = "Create a new department")
    @APIResponse(responseCode = "201", description = "Department created successfully")
    @APIResponse(responseCode = "400", description = "Invalid department data")
    @APIResponse(responseCode = "409", description = "Department with name already exists")
    public Response createDepartment(@Valid Department department) {
        logger.info("POST /api/departments - Creating department: " + department.getName());

        try {
            Department created = departmentService.createDepartment(department);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error creating department: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing department")
    @APIResponse(responseCode = "200", description = "Department updated successfully")
    @APIResponse(responseCode = "400", description = "Invalid department data")
    @APIResponse(responseCode = "404", description = "Department not found")
    @APIResponse(responseCode = "409", description = "Department with name already exists")
    public Response updateDepartment(@Parameter(description = "Department ID") @PathParam("id") Long id,
                                   @Valid Department department) {
        logger.info("PUT /api/departments/" + id);

        try {
            Department updated = departmentService.updateDepartment(id, department);
            DepartmentSummaryDto dto = new DepartmentSummaryDto(updated);
            return Response.ok(dto).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error updating department: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a department")
    @APIResponse(responseCode = "204", description = "Department deleted successfully")
    @APIResponse(responseCode = "404", description = "Department not found")
    public Response deleteDepartment(@Parameter(description = "Department ID") @PathParam("id") Long id) {
        logger.info("DELETE /api/departments/" + id);

        try {
            departmentService.deleteDepartment(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.severe("Error deleting department: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}