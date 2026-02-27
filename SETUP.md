# Employee Management App - Setup Instructions

This is a complete implementation of the Employee Management application with Angular frontend and Quarkus backend.

## Architecture

- **Backend**: Quarkus with PostgreSQL, Hibernate ORM, RESTful APIs
- **Frontend**: Angular with HTTP client for API communication
- **Database**: PostgreSQL with Flyway migrations

## Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.9+

## Backend Setup

1.  Navigate to the backend directory:
```bash
cd backend
```

2. Run the application:
```bash
./mvnw compile quarkus:dev
```

The backend will start on http://localhost:8080

### API Endpoints

- `GET /api/departments` - Get all departments with employee counts
- `POST /api/departments` - Create new department
- `DELETE /api/departments/{id}` - Delete department (moves employees to unassigned)
- `GET /api/employees` - Get all employees (supports search query param)
- `GET /api/employees/department/{id}` - Get employees by department
- `GET /api/employees/unassigned` - Get unassigned employees
- `POST /api/employees` - Create new employee
- `DELETE /api/employees/{id}` - Delete employee
- `PUT /api/departments/{id}` - Update department
- `PUT api/employees/{id}` - Update employee
- `PUT api/employees/{id}/department/{id}` - (update) Assign employee to department

## Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on http://localhost:4200

## Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Features Implemented

### Dashboard
- ✅ Lists all departments with employee counts
- ✅ Shows "Unassigned" department for employees without a department
- ✅ Add/delete departments (deleting moves employees to Unassigned)
- ✅ Search bar to find employees by name → click result navigates to their department

### Department View
- ✅ Lists employees in selected department
- ✅ Add/delete employees
- ✅ Search employees by name within department
- ✅ Click employee shows highlighted employee

### Backend Features
- ✅ Full CRUD for departments and employees
- ✅ Proper error handling and validation
- ✅ Database migrations with sample data -> The application uses an in-memory database as its data store, which is inherently temporary and exists only for the duration of the application's runtime. Database migrations have been implemented to reflect a real-world production setup, where a persistent database would require proper schema versioning and change management.
- ✅ CORS configuration for frontend
- ✅ Comprehensive unit tests

### Frontend Features
- ✅ Responsive design
- ✅ Error handling
- ✅ Loading states
- ✅ Modal dialogs for adding departments/employees
- ✅ Navigation between views
- ✅ Search functionality
- ✅ Unit tests

## Sample Data

The application comes with sample data:
- 4 departments: Engineering, Human Resources, Marketing, Finance
- 8 employees across departments, including some unassigned

## Key Design Decisions

1. **Department Deletion**: When a department is deleted, employees are moved to "Unassigned" rather than being deleted
2. **Search Navigation**: Searching employees from dashboard navigates to their department with employee highlighted
3. **Validation**: Email uniqueness enforced, proper input validation on both frontend and backend
4. **Error Handling**: Comprehensive error handling with user-friendly messages
5. **Testing**: Representative unit tests for both backend services and frontend components
