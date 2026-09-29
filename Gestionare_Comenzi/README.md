# Order Management System

A Java desktop application developed as a university project for managing clients, products, orders and bills using a layered architecture and a relational database.

## Description

The application provides functionality for managing the main entities involved in an order management system:

- clients;
- products;
- orders;
- bills.

The project separates data access, business logic, domain models and the user interface into different packages, making the application easier to organize and maintain.

## Features

### Client Management
- Add new clients
- Update client information
- Delete clients
- View existing clients

### Product Management
- Add new products
- Update product information
- Delete products
- View available products

### Order Management
- Create orders for existing clients
- Select products for an order
- Manage ordered quantities
- Process order information

### Bill Management
- Generate and store bill information associated with orders

## Technologies

- Java
- Maven
- JDBC
- SQL
- Java Swing
- Java Reflection
- IntelliJ IDEA

## Architecture

The application follows a layered architecture.

### Model Layer

Contains the main domain entities:

- `Client`
- `Product`
- `Order`
- `Bill`

### Data Access Layer (DAO)

The `dao` package is responsible for database operations.

It contains classes such as:

- `AbstractDAO`
- `ClientDAO`
- `ProductDAO`
- `OrderDAO`
- `BillDAO`

`AbstractDAO` provides reusable data-access functionality and uses Java Reflection to reduce duplicated code between DAO classes.

### Business Logic Layer (BLL)

The `bll` package contains the business logic of the application:

- `ClientBLL`
- `ProductBLL`
- `OrderBLL`
- `BillBLL`

This layer handles the operations performed on the application entities before interacting with the data-access layer.

### Presentation Layer

The `presentation` package contains the graphical user interface of the application.

The project includes forms for:

- client management;
- product management;
- order management.

### Database Connection

Database connectivity is handled through:

```text
ConnectionFactory
```

SQL resources are stored in:

```text
src/main/resources/SQL
```

## Project Structure

```text
Gestionare_Comenzi/
│
├── src/
├── pom.xml
├── .gitignore
│
└── pt-reflection-example-master/
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   ├── bll/
    │       │   ├── connection/
    │       │   ├── dao/
    │       │   ├── model/
    │       │   ├── presentation/
    │       │   └── start/
    │       └── resources/
    │           └── SQL/
    ├── pom.xml
    └── README.md
```

## Main Classes

### `Client`

Represents a client stored and managed by the application.

### `Product`

Represents a product that can be managed and included in orders.

### `Order`

Represents an order created for a client and associated with a product and quantity.

### `Bill`

Represents billing information generated from order-related operations.

## Example Application Flow

A typical workflow is:

```text
1. Add a client
        ↓
2. Add or select a product
        ↓
3. Create an order
        ↓
4. Process the order through the business logic layer
        ↓
5. Store the information in the database
        ↓
6. Generate the associated bill information
```

## How to Run

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Make sure Maven dependencies are loaded.
4. Configure the database connection used by `ConnectionFactory`.
5. Use the SQL files from `src/main/resources/SQL` to prepare the required database structure if necessary.
6. Run the application's start class from the `start` package.

## Purpose

This project was developed as a university assignment to practice:

- object-oriented programming in Java;
- layered application architecture;
- database interaction using JDBC;
- the DAO design pattern;
- business logic separation;
- Java Reflection;
- graphical user interfaces;
- Maven project management.
