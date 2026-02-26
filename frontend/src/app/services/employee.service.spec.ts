import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EmployeeService } from './employee.service';
import { EmployeeSummary, Employee } from '../models/employee.model';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EmployeeService]
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all employees', () => {
    const mockEmployees: EmployeeSummary[] = [
      { id: 1, fullName: 'John Doe', email: 'john@example.com', departmentId: 1, departmentName: 'Engineering' },
      { id: 2, fullName: 'Jane Smith', email: 'jane@example.com', departmentId: 2, departmentName: 'HR' }
    ];

    service.getAllEmployees().subscribe(employees => {
      expect(employees).toEqual(mockEmployees);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees');
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployees);
  });

  it('should search employees', () => {
    const mockEmployees: EmployeeSummary[] = [
      { id: 1, fullName: 'John Doe', email: 'john@example.com', departmentId: 1, departmentName: 'Engineering' }
    ];

    service.searchEmployees('john').subscribe(employees => {
      expect(employees).toEqual(mockEmployees);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees?search=john');
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployees);
  });

  it('should fetch employees by department', () => {
    const mockEmployees: EmployeeSummary[] = [
      { id: 1, fullName: 'John Doe', email: 'john@example.com', departmentId: 1, departmentName: 'Engineering' }
    ];

    service.getEmployeesByDepartment(1).subscribe(employees => {
      expect(employees).toEqual(mockEmployees);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees/department/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployees);
  });

  it('should fetch unassigned employees', () => {
    const mockEmployees: EmployeeSummary[] = [
      { id: 3, fullName: 'Bob Wilson', email: 'bob@example.com', departmentId: null, departmentName: 'Unassigned' }
    ];

    service.getEmployeesByDepartment(null).subscribe(employees => {
      expect(employees).toEqual(mockEmployees);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees/unassigned');
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployees);
  });

  it('should create employee', () => {
    const newEmployee = { fullName: 'Alice Johnson', email: 'alice@example.com', departmentId: 1 };
    const createdEmployee: Employee = { id: 4, fullName: 'Alice Johnson', email: 'alice@example.com', departmentId: 1 };

    service.createEmployee(newEmployee).subscribe(employee => {
      expect(employee).toEqual(createdEmployee);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newEmployee);
    req.flush(createdEmployee);
  });

  it('should delete employee', () => {
    service.deleteEmployee(1).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/employees/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should assign employee to department', () => {
    const updatedEmployee: Employee = { id: 1, fullName: 'John Doe', departmentId: 2 };

    service.assignEmployeeToDepartment(1, 2).subscribe(employee => {
      expect(employee).toEqual(updatedEmployee);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/employees/1/department/2');
    expect(req.request.method).toBe('PUT');
    req.flush(updatedEmployee);
  });
});