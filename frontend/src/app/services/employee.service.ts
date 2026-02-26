import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Employee, EmployeeSummary, CreateEmployeeRequest } from '../models/employee.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private readonly baseUrl = 'http://localhost:8080/api/employees';

  constructor(private http: HttpClient) {}

  getAllEmployees(search?: string): Observable<EmployeeSummary[]> {
    let params = new HttpParams();
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<EmployeeSummary[]>(this.baseUrl, { params })
      .pipe(catchError(this.handleError));
  }

  getEmployeesByDepartment(departmentId: number | null, search?: string): Observable<EmployeeSummary[]> {
    let url: string;
    let params = new HttpParams();

    if (departmentId === null) {
      url = `${this.baseUrl}/unassigned`;
    } else {
      url = `${this.baseUrl}/department/${departmentId}`;
    }

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<EmployeeSummary[]>(url, { params })
      .pipe(catchError(this.handleError));
  }

  searchEmployees(search: string): Observable<EmployeeSummary[]> {
    const params = new HttpParams().set('search', search);
    return this.http.get<EmployeeSummary[]>(this.baseUrl, { params })
      .pipe(catchError(this.handleError));
  }

  createEmployee(employee: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, employee)
      .pipe(catchError(this.handleError));
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  assignEmployeeToDepartment(employeeId: number, departmentId: number): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/${employeeId}/department/${departmentId}`, {})
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = `Server error: ${error.status} ${error.statusText}`;
      }
    }

    console.error('Employee service error:', errorMessage);
    return throwError(() => errorMessage);
  }
}
