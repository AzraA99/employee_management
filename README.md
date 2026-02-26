# Employee Management App

Build a web app to manage employees organized into departments.

**Stack:** Angular (frontend) - Quarkus or Spring Boot (backend) - PostgreSQL + Hibernate

---

## Requirements

- **Departments** have unique names and are independent of each other
- **Employees** have contact info (full name, address, phone, email) and belong to at most one department
- Backend must support full CRUD for both entities

---

## UI Requirements

### Dashboard
- List all departments with employee counts
- Show "Unassigned" Department for employees without a department
- Add/delete departments (deleting moves employees to Unassigned)
- Search bar: find employees by name → click result to navigate to their department

![Dashboard](documentation/dashboard.jpg) ![Search](documentation/dashboard-search.jpg)

### Department View
- List employees in selected department
- Add/delete employees
- Search employees by name (shows name + department) → click to view employee

![Department View](documentation/department-view.jpg)

*Editing existing departments/employees via frontend is NOT required.*

---

## Guidelines

- Starter templates provided in `frontend/` (Angular) and `backend/` (Quarkus) — use them or build your own
- Keep it simple and clean
- Include a few representative tests (backend, frontend, not comprehensive)
- UI mockups are suggestions only
- Share your repo link when complete; we'll discuss your solution in a code-review style interview
