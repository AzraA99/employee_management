import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentService } from '../services/department.service';
import { EmployeeService } from '../services/employee.service';
import { Department } from '../models/department.model';
import { EmployeeSummary, CreateEmployeeRequest } from '../models/employee.model';

@Component({
  selector: 'app-department-view',
  standalone: false,
  templateUrl: './department-view.component.html',
  styleUrls: ['./department-view.component.scss']
})
export class DepartmentViewComponent implements OnInit {
  departmentId: number | null = null;
  department: Department | null = null;
  employees: EmployeeSummary[] = [];
  allEmployees: EmployeeSummary[] = [];
  searchQuery = '';
  loading = false;
  error = '';
  showAddEmployeeModal = false;
  newEmployee: CreateEmployeeRequest = {
    fullName: '',
    address: '',
    phone: '',
    email: ''
  };
  isUnassignedDepartment = false;
  highlightedEmployeeId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private departmentService: DepartmentService,
    private employeeService: EmployeeService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id === 'unassigned') {
        this.isUnassignedDepartment = true;
        this.departmentId = null;
        this.department = { id: null, name: 'Unassigned' };
      } else {
        this.isUnassignedDepartment = false;
        this.departmentId = +id;
        this.loadDepartment();
      }
      this.loadEmployees();
    });

    this.route.queryParams.subscribe(params => {
      this.highlightedEmployeeId = params['employeeId'] ? +params['employeeId'] : null;
    });
  }

  loadDepartment(): void {
    if (this.departmentId === null) return;

    this.departmentService.getDepartmentById(this.departmentId).subscribe({
      next: (department) => {
        this.department = department;
      },
      error: (error) => {
        this.error = error;
        console.error('Error loading department:', error);
      }
    });
  }

  loadEmployees(): void {
    this.loading = true;
    this.error = '';

    this.employeeService.getEmployeesByDepartment(this.departmentId).subscribe({
      next: (employees) => {
        this.allEmployees = employees;
        this.filterEmployees();
        this.loading = false;
      },
      error: (error) => {
        this.error = error;
        this.loading = false;
        console.error('Error loading employees:', error);
      }
    });
  }

  filterEmployees(): void {
    if (!this.searchQuery.trim()) {
      this.employees = [...this.allEmployees];
    } else {
      const query = this.searchQuery.toLowerCase();
      this.employees = this.allEmployees.filter(emp =>
        emp.fullName.toLowerCase().includes(query)
      );
    }
  }

  onSearch(): void {
    this.filterEmployees();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.filterEmployees();
  }

  showAddEmployee(): void {
    this.showAddEmployeeModal = true;
    this.newEmployee = {
      fullName: '',
      address: '',
      phone: '',
      email: '',
      department: this.departmentId ? { id: this.departmentId } : null
    };
  }

  hideAddEmployee(): void {
    this.showAddEmployeeModal = false;
  }

  addEmployee(): void {
    if (!this.newEmployee.fullName.trim()) {
      return;
    }

    this.newEmployee.department = this.departmentId ? { id: this.departmentId } : null;

    this.employeeService.createEmployee(this.newEmployee).subscribe({
      next: () => {
        this.loadEmployees();
        this.hideAddEmployee();
      },
      error: (error) => {
        console.error('Error creating employee:', error);
        this.error = error;
      }
    });
  }

  deleteEmployee(employee: EmployeeSummary, event: Event): void {
    event.stopPropagation();

    if (!confirm(`Are you sure you want to delete "${employee.fullName}"?`)) {
      return;
    }

    this.employeeService.deleteEmployee(employee.id).subscribe({
      next: () => {
        this.loadEmployees();
      },
      error: (error) => {
        console.error('Error deleting employee:', error);
        this.error = error;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  isEmployeeHighlighted(employeeId: number): boolean {
    return this.highlightedEmployeeId === employeeId;
  }
}