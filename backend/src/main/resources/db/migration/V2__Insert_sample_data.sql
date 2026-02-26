-- Insert sample departments
INSERT INTO departments (name) VALUES
    ('Engineering'),
    ('Human Resources'),
    ('Marketing'),
    ('Finance');

-- Insert sample employees
INSERT INTO employees (full_name, address, phone, email, department_id) VALUES
    ('John Doe', '123 Main St, Anytown, USA', '+1-555-0101', 'john.doe@company.com', 1),
    ('Jane Smith', '456 Oak Ave, Somewhere, USA', '+1-555-0102', 'jane.smith@company.com', 1),
    ('Bob Johnson', '789 Pine St, Anywhere, USA', '+1-555-0103', 'bob.johnson@company.com', 2),
    ('Alice Brown', '321 Elm St, Nowhere, USA', '+1-555-0104', 'alice.brown@company.com', 3),
    ('Charlie Wilson', '654 Cedar Ave, Everywhere, USA', '+1-555-0105', 'charlie.wilson@company.com', 1),
    ('Diana Davis', '987 Birch St, Somewhere Else, USA', '+1-555-0106', 'diana.davis@company.com', 4),
    ('Eve Anderson', '147 Maple Ave, Another Place, USA', '+1-555-0107', 'eve.anderson@company.com', NULL),
    ('Frank Miller', '258 Spruce St, Different Town, USA', '+1-555-0108', 'frank.miller@company.com', NULL);