# Nail Salon Management System

A Java desktop application developed as a university project for managing the activity of a nail salon.

## Description

The application provides a graphical interface for different types of users and includes functionality related to authentication, salon management and appointment/calendar access.

The project is organized around several user roles, including administrators, salon staff and clients, with dedicated screens for each type of user.

## Main Features

- User authentication
- Role-based access to the application
- Administrator dashboard
- Staff / manicurist dashboard
- Client home page
- User calendar and appointment-related interface
- Salon and user data management
- Database connectivity

## Technologies

- Java
- Java Swing
- JDBC
- SQL / relational database
- IntelliJ IDEA

## Project Structure

The main source code is located in the `src` directory.

```text
GestionareSalonUnghii/
│
├── src/
│   ├── AdminDashboard.java
│   ├── AutentificareUtilizator.java
│   ├── Conexiune.java
│   ├── HomePageMani.java
│   ├── HomePageUser.java
│   ├── Main.java
│   ├── ManiDashboard.java
│   ├── Salon.java
│   ├── User.java
│   └── UserCalendarPage.java
│
├── .gitignore
├── proiect_is.iml
├── 01_T_SWDP_System_Requirements_Template_RO.doc
├── 02_T_SWDP_Analysis&Design_Template__RO.doc
├── Power_Point.pptx
└── README.md
```

## Main Classes

### `Main.java`

Entry point of the application.

### `AutentificareUtilizator.java`

Handles the authentication interface and the login flow for application users.

### `AdminDashboard.java`

Provides the main dashboard for administrator-level functionality.

### `ManiDashboard.java`

Provides a dedicated dashboard for salon staff / manicurists.

### `HomePageUser.java`

Represents the main interface available to a regular client/user.

### `HomePageMani.java`

Represents the home interface used by salon staff.

### `UserCalendarPage.java`

Provides a calendar-based interface for users and supports appointment-related interaction.

### `User.java`

Represents user-related data used by the application.

### `Salon.java`

Represents salon-related information and application data.

### `Conexiune.java`

Handles the application's database connection.

## Application Flow

A simplified application flow is:

```text
Start application
      ↓
User authentication
      ↓
Role identification
      ↓
┌─────────────────┬─────────────────┬─────────────────┐
│ Administrator   │ Salon Staff     │ Client / User   │
│ Dashboard       │ Dashboard       │ Home Page       │
└─────────────────┴─────────────────┴─────────────────┘
                                      ↓
                                 Calendar / Appointments
```

## Example Usage

1. Start the application by running `Main.java`.
2. Log in through the authentication screen.
3. The application opens the appropriate interface based on the authenticated user.
4. Administrators can access the administrator dashboard.
5. Salon staff can access their dedicated dashboard.
6. Clients can access their home page and calendar-related functionality.

## How to Run

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Configure the database connection used by `Conexiune.java`.
4. Make sure the required database is available.
5. Run the `Main.java` class.
6. Use the authentication screen to access the application.

## Documentation

The repository also contains project documentation:

- `01_T_SWDP_System_Requirements_Template_RO.doc` – system requirements documentation
- `02_T_SWDP_Analysis&Design_Template__RO.doc` – analysis and design documentation
- `Power_Point.pptx` – project presentation

## Purpose

This project was developed as a university assignment to practice:

- object-oriented programming in Java;
- desktop graphical user interfaces;
- user authentication;
- role-based application navigation;
- database connectivity with JDBC;
- software requirements and design documentation.
