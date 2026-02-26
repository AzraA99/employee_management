package com.dedalus.interview.dto;

import com.dedalus.interview.entity.Department;

public class DepartmentSummaryDto {
    private Long id;
    private String name;
    private int employeeCount;

    public DepartmentSummaryDto() {}

    public DepartmentSummaryDto(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.employeeCount = department.getEmployeeCount();
    }

    public DepartmentSummaryDto(Long id, String name, int employeeCount) {
        this.id = id;
        this.name = name;
        this.employeeCount = employeeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }
}