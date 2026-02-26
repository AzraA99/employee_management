package com.dedalus.interview.dto;

import com.dedalus.interview.entity.Employee;

public class EmployeeSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Long departmentId;
    private String departmentName;

    public EmployeeSummaryDto() {}

    public EmployeeSummaryDto(Employee employee) {
        this.id = employee.getId();
        this.fullName = employee.getFullName();
        this.email = employee.getEmail();
        this.phone = employee.getPhone();
        this.address = employee.getAddress();
        this.departmentId = employee.getDepartmentId();
        this.departmentName = employee.getDepartmentName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}