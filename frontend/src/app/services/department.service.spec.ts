import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DepartmentService } from './department.service';
import { DepartmentSummary, Department } from '../models/department.model';

describe('DepartmentService', () => {
  let service: DepartmentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DepartmentService]
    });
    service = TestBed.inject(DepartmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all departments', () => {
    const mockDepartments: DepartmentSummary[] = [
      { id: 1, name: 'Engineering', employeeCount: 5 },
      { id: 2, name: 'HR', employeeCount: 3 }
    ];

    service.getAllDepartments().subscribe(departments => {
      expect(departments).toEqual(mockDepartments);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/departments');
    expect(req.request.method).toBe('GET');
    req.flush(mockDepartments);
  });

  it('should fetch department by id', () => {
    const mockDepartment: Department = { id: 1, name: 'Engineering' };

    service.getDepartmentById(1).subscribe(department => {
      expect(department).toEqual(mockDepartment);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/departments/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockDepartment);
  });

  it('should create department', () => {
    const newDepartment = { name: 'Marketing' };
    const createdDepartment: Department = { id: 3, name: 'Marketing' };

    service.createDepartment(newDepartment).subscribe(department => {
      expect(department).toEqual(createdDepartment);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/departments');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newDepartment);
    req.flush(createdDepartment);
  });

  it('should delete department', () => {
    service.deleteDepartment(1).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/departments/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});