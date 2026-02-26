export interface Employee {
  id: number | null;
  fullName: string;
  address?: string;
  phone?: string;
  email?: string;
  departmentId?: number | null;
  departmentName?: string;
}

export interface EmployeeSummary {
  id: number;
  fullName: string;
  email?: string;
  phone?: string;
  address?: string;
  departmentId?: number | null;
  departmentName?: string;
}

export interface CreateEmployeeRequest {
  fullName: string;
  address?: string;
  phone?: string;
  email?: string;
  department?: { id: number | null } | null;
}