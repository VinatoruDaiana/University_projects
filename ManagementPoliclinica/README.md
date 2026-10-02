# ManagementPoliclinica

A Java desktop application developed as a university database project for managing the activity of a medical clinic.

## Description

The application supports several types of clinic users and provides functionality for managing patients, employees, schedules, appointments, medical activity and financial information.

The project uses a MySQL database and a Java graphical interface.

## Main Features

- User authentication
- Patient registration and management
- Appointment creation
- Employee management
- Work schedule management
- Vacation / leave management
- Medical activity management
- Patient history access
- Viewing scheduled patients
- Salary and financial information
- Profit reports by doctor and specialization
- Receipt generation
- Administrative and super-admin functionality

## User Roles

The application contains dedicated functionality for roles such as:

- Administrator
- Super Administrator
- Doctor
- Nurse
- Receptionist
- Financial Expert
- Inspector

## Technologies

- Java
- Java Swing
- MySQL
- JDBC
- IntelliJ IDEA
- SQL

## Project Structure

```text
ManagementPoliclinica/
├── InterfataBD/
│   ├── src/
│   │   └── Main/
│   ├── lib/
│   └── Main_1.iml
├── ProiectBD.sql
└── README.md
```

The `src/Main` package contains the application's graphical interfaces and business functionality, while `ProiectBD.sql` contains the database definition used by the project.

## Examples of Implemented Components

Some of the main classes include:

- `Clinica.java`
- `ConexiuneSQL.java`
- `CreeazaProgramare.java`
- `AdaugarePacient.java`
- `CreeazaRaport.java`
- `EmiteBon.java`
- `ActivitateAdmin.java`
- `ActivitateSuperAdmin.java`
- `VeziIstoricPacientMedic.java`

## How to Run

1. Import `ProiectBD.sql` into MySQL.
2. Open the Java project from the `InterfataBD` directory in IntelliJ IDEA.
3. Make sure the MySQL JDBC connector is available.
4. Configure the database connection if necessary.
5. Run the application from the appropriate main/login class.

## Purpose

This project was developed to practice relational database design, SQL, JDBC connectivity and the implementation of a role-based Java desktop application.
