# Transport Reporting System - Backend

A robust enterprise-grade backend built with Java 17 and Spring Boot 3.5.7, designed to handle transportation reports, company management, and multi-role user workflows with high security.

##  Core Technologies

- **Java 17**: Modern language features and performance.
- **Spring Boot 3.5.7**: High-performance backend framework.
- **Spring Security & JWT**: Stateless authentication with role-based access control (RBAC).
- **PostgreSQL**: Reliable relational database (via Spring Data JPA/Hibernate).
- **Two-Factor Authentication (2FA)**: Email-based secondary verification.
- **Lombok**: Reduced boilerplate code for cleaner entities and DTOs.
- **Maven**: Dependency management and build automation.

##  Key Features

- **RBAC (Role-Based Access Control)**: Secure access for `SUPER_ADMIN`, `COMPANY_ADMIN`, and `USER`.
- **Enhanced Security**:
  - JWT Tokens with interceptor support.
  - Automatic constraint cleanup (see `DatabaseInitializer.java`).
  - 2FA verification via email.
- **Global Search**: High-performance cross-entity search supporting numeric IDs and descriptions.
- **Dashboard API**: Real-time business statistics tailored to the user's role.
- **Pagination & Sorting**: Efficient data retrieval for all list views.
- **Privacy First**: Reporter names are hidden from Company Admins to ensure anonymous and unbiased feedback.

##  Architecture Overview

The backend follows the standard **Controller-Service-Repository** (Layered) pattern:

1.  **Controller Layer**: Handles HTTP requests, input validation, and maps logic to service methods.
2.  **Service Layer**: Contains the core business logic, transaction management, and coordinates between components.
3.  **Repository Layer**: Direct database interaction using Spring Data JPA.
4.  **Entity Layer**: JPA-mapped models representing database tables.
5.  **DTOs (Data Transfer Objects)**: Decouples internal entity structure from external API responses.

##  Installation & Setup

### Prerequisites
- Java 17+ installed.
- PostgreSQL 15+ installed.
- Maven installed.

### Configuration
1.  Navigate to `TransportReportingSystem/src/main/resources/`.
2.  Update `application.properties` with your database credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/transport_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
3.  Ensure your email settings are configured for 2FA/Password Reset.

### Running the Project
```bash
./mvnw spring-boot:run
```

