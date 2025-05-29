# TodoManager

## Overview

TodoManager is a task management application built with Spring Boot and PostgreSQL. 
The application focuses on centralized task management where 
- administrators can: 
  - create projects, 
  - assign tasks to users, 
  - and manage the workflow, 
- while regular users can: 
  - view and interact with tasks assigned to them.

## Features

### Administrator Features
- Create, edit, and delete projects
- Create tasks with different types (MEETING, INTERVIEW, ISSUE, BUG, FEATURE, TASK)
- Assign tasks to users
- View all tasks assigned to a specific user
- View all tasks for a specific project
- Manage task statuses

### User Features
- View all tasks assigned to them
- See all projects they are part of
- View task details

## Technology Stack

- **Backend**: Java 23 with Spring Boot 3.4.3
- **Database**: PostgreSQL
- **Authentication**: JWT-based authentication
- **Documentation**: Swagger/OpenAPI
- **Testing**: JUnit with test coverage and H2
- **Build Tool**: Maven
- **Containerization**: Docker and Docker Compose

## API Documentation

The API is documented using Swagger/OpenAPI. Once the application is running, you can access the API documentation at:
```
http://localhost:8083/swagger-ui/index.html
```

## Project Structure

The project follows a clean architecture with separation of concerns:

- **Entity Layer**: Core domain models (Project, Task, User)
- **Repository Layer**: Database access interfaces
- **Service Layer**: Business logic implementation
- **Controller Layer**: REST API endpoints
- **DTO Layer**: Data transfer objects for API interaction
- **Security Layer**: Authentication and authorization

#### Separation of concerns:
- admin - for all admin features
  - admin.services, admin.controllers, admin.dto
- user - for all user features
- security - for security features
- data - for shared data access and entities

- libraries
  - validation - adapter for jakarta validation
  - datetime - testable adapter for java.time
  - seeder - interface used for scripts to run before each application start

## Database Schema

The application uses the following main entities:
- **Users**: Store user information and credentials
- **Roles**: Define user roles (ADMIN, USER)
- **Projects**: Contain task groupings, has assigned owner
- **Tasks**: Individual work items with assignees, descriptions, and types

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 23
- Maven

### Running with Docker
1. Clone the repository
2. Set up environment variables in `.env` file:
   ```
   POSTGRES_USER=your_postgres_user
   POSTGRES_PASSWORD=your_postgres_password
   POSTGRES_DB=todomanager
   JWT_SECRET=your_jwt_secret
   ```
3. Build and run the application:
   ```bash
   mvn clean install package && docker compose up -d
   ```
4. The application will be available at http://localhost:8083

### Running Locally
1. Clone the repository
2. Configure environmental settings in `.env.local`
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing

The project includes comprehensive unit and integration tests. Test coverage reports are available in the `htmlReport` directory.

To run tests:
```bash
mvn test
```

## Migrations

The project uses Flyway for database migrations. Migrations are located in the `src/main/resources/db/migration` directory. To apply migrations, run:
```bash
mvn flyway:migrate
```

default config `./flyway.conf` is used