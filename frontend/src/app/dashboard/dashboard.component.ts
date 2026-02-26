import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DepartmentService } from '../services/department.service';
import { EmployeeService } from '../services/employee.service';
import { DepartmentSummary } from '../models/department.model';
import { EmployeeSummary } from '../models/employee.model';

@Component({
  selector: 'dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  departments: DepartmentSummary[] = [];
  searchResults: EmployeeSummary[] = [];
  searchQuery = '';
  loading = false;
  error = '';
  showAddDepartmentModal = false;
  newDepartmentName = '';

  constructor(
    private departmentService: DepartmentService,
    private employeeService: EmployeeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  loadDepartments(): void {
    this.loading = true;
    this.error = '';

    this.departmentService.getAllDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
        this.loading = false;
      },
      error: (error) => {
        this.error = error;
        this.loading = false;
        console.error('Error loading departments:', error);
      }
    });
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      return;
    }

    this.employeeService.searchEmployees(this.searchQuery).subscribe({
      next: (employees) => {
        this.searchResults = employees;
      },
      error: (error) => {
        console.error('Error searching employees:', error);
        this.searchResults = [];
      }
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchResults = [];
  }

  navigateToEmployee(employee: EmployeeSummary): void {
    if (employee.departmentId) {
      this.router.navigate(['/department', employee.departmentId], {
        queryParams: { employeeId: employee.id }
      });
    } else {
      this.router.navigate(['/department', 'unassigned'], {
        queryParams: { employeeId: employee.id }
      });
    }
  }

  navigateToDepartment(department: DepartmentSummary): void {
    if (department.id === null) {
      this.router.navigate(['/department', 'unassigned']);
    } else {
      this.router.navigate(['/department', department.id]);
    }
  }

  showAddDepartment(): void {
    this.showAddDepartmentModal = true;
    this.newDepartmentName = '';
  }

  hideAddDepartment(): void {
    this.showAddDepartmentModal = false;
    this.newDepartmentName = '';
  }

  addDepartment(): void {
    if (!this.newDepartmentName.trim()) {
      return;
    }

    this.departmentService.createDepartment({ name: this.newDepartmentName.trim() }).subscribe({
      next: () => {
        this.loadDepartments();
        this.hideAddDepartment();
      },
      error: (error) => {
        console.error('Error creating department:', error);
        this.error = error;
      }
    });
  }

  deleteDepartment(department: DepartmentSummary, event: Event): void {
    event.stopPropagation();

    if (department.id === null) {
      return;
    }

    if (!confirm(`Are you sure you want to delete "${department.name}"? All employees will be moved to Unassigned.`)) {
      return;
    }

    this.departmentService.deleteDepartment(department.id).subscribe({
      next: () => {
        this.loadDepartments();
      },
      error: (error) => {
        console.error('Error deleting department:', error);
        this.error = error;
      }
    });
  }
}
