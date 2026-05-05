# Book Shop Backend

A robust and secure RESTful backend API for an e-commerce book shop application. Built with Spring Boot 3, Spring Security, and PostgreSQL, this backend provides a complete solution for managing a book inventory, user shopping carts, order processing, and role-based authentication.

## Features

*   **Authentication & Authorization:** Secure JWT-based authentication using HTTP-only cookies. Differentiated access controls for regular `USER`s and `ADMIN`s.
*   **Inventory Management:** Admins can add, update, and remove books from the store.
*   **Shopping Cart:** Users can maintain a shopping cart, add items, update quantities, and remove items.
*   **Order Processing:** Checkout capabilities to convert a shopping cart into an order. Admins can view all orders and update statuses (approve or cancel).
*   **Comprehensive Testing:** Fully covered by integration tests utilizing MockMvc and Mockito to guarantee endpoint security and validation.

## Technologies Used

*   **Java 17**
*   **Spring Boot 3.4.5**
    *   Spring Web (REST APIs)
    *   Spring Data JPA (Hibernate ORM)
    *   Spring Security (JWT, CSRF, Role-Based Access)
    *   Spring Boot Validation (Jakarta Bean Validation)
*   **PostgreSQL** (Relational Database)
*   **Gradle** (Kotlin DSL)
*   **JUnit 5 & Mockito** (Testing)

## Prerequisites

*   **Java 17** or higher installed.
*   **PostgreSQL** installed and running on your local machine (or Docker).
*   An IDE (IntelliJ IDEA, VS Code, or Eclipse) is recommended.

## Installation & Setup

1.  **Clone or Open the Project**
    Navigate to the project root directory (`book-shop-backend`).

2.  **Configure the Database**
    Open `src/main/resources/application.properties` (or `.yml`) and ensure your PostgreSQL database credentials are correct. For example:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/bookshop
    spring.datasource.username=your_db_username
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update
    ```
    *Note: Create a database named `bookshop` in your PostgreSQL instance before running.*

3.  **Configure JWT Secret**
    Ensure your application properties contain a secure JWT secret and expiration time:
    ```properties
    krekno.app.jwtSecret=YourVerySecureSecretKeyThatIsAtLeast256BitsLong
    krekno.app.jwtExpirationMs=86400000
    ```

## Running the Application

You can start the Spring Boot server using the Gradle wrapper from your terminal:

```bash
# On Windows
.\gradlew.bat bootRun

# On macOS/Linux
./gradlew bootRun
```

The server will start on `http://localhost:8080`.

## Running the Tests

The project includes a robust suite of automated integration tests that verify HTTP status codes, security barriers, and input validation.

To run the test suite:

```bash
# On Windows
.\gradlew.bat test

# On macOS/Linux
./gradlew test
```

To view a detailed HTML test report, you can navigate to `build/reports/tests/test/index.html` after the test task completes.

### API Quick Overview
*   `/api/auth/**` - Signup, Signin, Refresh, Logout
*   `/api/books/**` - View books, Add/Edit/Delete books (Admin)
*   `/api/cart/**` - Manage shopping cart
*   `/api/order/**` - Place orders, View orders, Approve/Cancel orders (Admin)
