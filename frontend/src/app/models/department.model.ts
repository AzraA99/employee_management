export interface Department {
  id: number | null;
  name: string;
  employeeCount?: number;
}

export interface DepartmentSummary {
  id: number | null;
  name: string;
  employeeCount: number;
}

export interface CreateDepartmentRequest {
  name: string;
}