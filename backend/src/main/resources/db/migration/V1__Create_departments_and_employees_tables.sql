-- Create departments table
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create employees table
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_full_name ON employees(full_name);
CREATE INDEX idx_departments_name ON departments(name);