import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { DashboardComponent } from './dashboard.component';
import { DepartmentService } from '../services/department.service';
import { EmployeeService } from '../services/employee.service';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let departmentService: jasmine.SpyObj<DepartmentService>;
  let employeeService: jasmine.SpyObj<EmployeeService>;

  beforeEach(async () => {
    const departmentServiceSpy = jasmine.createSpyObj('DepartmentService', ['getAllDepartments', 'createDepartment', 'deleteDepartment']);
    const employeeServiceSpy = jasmine.createSpyObj('EmployeeService', ['searchEmployees']);

    await TestBed.configureTestingModule({
      declarations: [DashboardComponent],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        { provide: DepartmentService, useValue: departmentServiceSpy },
        { provide: EmployeeService, useValue: employeeServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    departmentService = TestBed.inject(DepartmentService) as jasmine.SpyObj<DepartmentService>;
    employeeService = TestBed.inject(EmployeeService) as jasmine.SpyObj<EmployeeService>;

    // Mock service calls
    departmentService.getAllDepartments.and.returnValue(of([
      { id: 1, name: 'Engineering', employeeCount: 5 },
      { id: 2, name: 'HR', employeeCount: 3 }
    ]));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load departments on init', () => {
    expect(departmentService.getAllDepartments).toHaveBeenCalled();
    expect(component.departments.length).toBe(2);
    expect(component.departments[0].name).toBe('Engineering');
  });

  it('should search employees when search query is entered', () => {
    const mockSearchResults = [
      { id: 1, fullName: 'John Doe', departmentName: 'Engineering' }
    ];
    employeeService.searchEmployees.and.returnValue(of(mockSearchResults));

    component.searchQuery = 'john';
    component.onSearch();

    expect(employeeService.searchEmployees).toHaveBeenCalledWith('john');
    expect(component.searchResults).toEqual(mockSearchResults);
  });

  it('should clear search results when search is cleared', () => {
    component.searchResults = [{ id: 1, fullName: 'John Doe', departmentName: 'Engineering' }];
    component.searchQuery = 'john';

    component.clearSearch();

    expect(component.searchQuery).toBe('');
    expect(component.searchResults).toEqual([]);
  });
});
